package com.tazzie02.tazbot.commands.owner;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class EvalCommand implements Command {
	
	private ScriptEngine engine;

	public EvalCommand() {
		engine = new ScriptEngineManager().getEngineByName("nashorn");
		
		try {
			engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");
		}
		catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String code = StringUtils.join(args, " ", 1, args.length);
		String inputMessage = "*Input:*```js\n" + code + "```";
		
		// This does not go through SendMessage so that it can be
		// sent without async. This means it can be instantly returned
		// and edited later. However it will not be logged.
		// There is the possibility that this might not be sent due to
		// rate limit, but it is a developer only command anyway so they
		// should be aware of this.
		try {
			Message message = e.getChannel().sendMessage(inputMessage).block();
			
			engine.put("event", e);
			engine.put("channel", e.getChannel());
			engine.put("args", args);
			engine.put("api", e.getJDA());
			engine.put("jda", e.getJDA());
			engine.put("guild", e.getGuild());
			engine.put("author", e.getAuthor());
			engine.put("me", e.getAuthor());
			engine.put("self", e.getJDA().getSelfUser());
			engine.put("bot", e.getJDA().getSelfUser());
			
			Thread eval = new Thread(new Runnable() {
				@Override
				public void run() {
					Object result = null;
					try {
						result = engine.eval(
								"(function() {" +
										"with (imports) {" +
										code +
										"}" +
								"})();");
					}
					catch (ScriptException ex) {
						message.editMessage(inputMessage + "\n" + "*Encountered Exception:*```\n" + ex.getMessage() + "```");
						return;
					}
					
					String outputMessage;
					if (result == null) {
						outputMessage = "`Completed.`";
					}
					else if (result.toString().length() >= 2000) {
						outputMessage = "*Result exceeds max message size.*";
					}
					else {
						outputMessage = "*Output:*```\n" + result.toString()
									.replace("`", "\\`")
									.replace("@everyone", "@\u180Eeveryone")
									.replace("@here", "@\u180Ehere")
									+ "```";
					}
					if (outputMessage.length() + inputMessage.length() >= 2000) {
						SendMessage.sendMessage(e, outputMessage);
					}
					else {
						// Update the message
						message.editMessage(inputMessage + "\n" + outputMessage);
					}
				}
			});
			
			Thread evalTimer = new Thread(new Runnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					}
					catch (InterruptedException ignored) {}
					
					if (eval.isAlive()) {
						eval.stop();
						SendMessage.sendMessage(e, inputMessage + "\n" + "*Result took longer than 5 seconds to complete.*");
					}
				}
			});
			
			eval.start();
			evalTimer.start();
		} catch (RateLimitedException ignored) {}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("eval");
	}

	@Override
	public String getDescription() {
		return "Execute Java or Javascript code.";
	}

	@Override
	public String getName() {
		return "Evaluate Command";
	}

	@Override
	public String getUsageInstructions() {
		return "eval <code> - Execute code.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
