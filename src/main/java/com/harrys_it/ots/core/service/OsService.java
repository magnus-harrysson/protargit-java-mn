package com.harrys_it.ots.core.service;

import com.harrys_it.ots.core.model.Os;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Singleton
public class OsService {
	private final BroadcasterService broadcasterService;
	private static final Logger LOGGER = LoggerFactory.getLogger(OsService.class);

	public OsService(BroadcasterService broadcasterService) {
		this.broadcasterService = broadcasterService;
	}

	public synchronized String command(Os os, String... args) throws InterruptedException {
		if(Os.of(os)) {
			var containsExtraArgument = (os.extraArgumentAllowed() && !args[0].isBlank() && !args[0].isEmpty());
			var res = containsExtraArgument
					? executeCommandWithResponse(os.getCommand() + args[0])
					: executeCommandWithResponse(os.getCommand());

			if(os.emitValueOnChange()) {
				broadcasterService.updateValues();
			}

			return res;
		} else {
			throw new IllegalArgumentException("OS Enum is not implemented");
		}
	}

	private String executeCommandWithResponse(String cmdToExecute) throws InterruptedException {
		Process p;
		var res = new String[3]; // max number of lines to read and return to caller.
		try {
			p = Runtime.getRuntime().exec(cmdToExecute);
			var stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));

			var i = 0;
			String s;
			while((s = stdin.readLine()) != null){
				res[i++] = s;
			}

			p.waitFor();
			p.destroy();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			throw new InterruptedException();
		}

		return res[0]; // For now. Do only return the first line/row.
	}

	public void resetGPIO()  {
		try {
			command(Os.GPIO_4_LOW);
			command(Os.GPIO_3_LOW);
			command(Os.GPIO_2_LOW);
			command(Os.GPIO_1_LOW);

			command(Os.GPIO_4_OUT);
			command(Os.GPIO_3_OUT);
			command(Os.GPIO_2_OUT);
			command(Os.GPIO_1_OUT);
		} catch (InterruptedException e) {
			LOGGER.debug("Failed to reset GPIO pins", e);
			Thread.currentThread().interrupt();
		}
	}

	public void playAudioWav(String filename) throws InterruptedException {
		var file = new File("./audio/" + filename);
		if(!file.exists()){
			LOGGER.debug("{} does not exist!", filename);
			return;
		}
		command(Os.PLAY_WAV_AUDIO,  filename);
	}

	public void playAudioMp3(String filename) throws InterruptedException {
		var file = new File("./audio/" + filename);
		if(!file.exists()){
			LOGGER.debug("{} does not exist!", filename);
			return;
		}
		command(Os.PLAY_MP3_AUDIO,  filename);
	}
}
