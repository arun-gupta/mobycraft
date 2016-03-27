package org.redfrog404.mobycraft.commands;

import static org.redfrog404.mobycraft.commands.ContainerListCommands.getAll;
import static org.redfrog404.mobycraft.commands.DockerCommands.arg1;
import static org.redfrog404.mobycraft.commands.DockerCommands.checkIfArgIsNull;
import static org.redfrog404.mobycraft.commands.DockerCommands.convertBytesAndMultiply;
import static org.redfrog404.mobycraft.commands.DockerCommands.getDockerClient;
import static org.redfrog404.mobycraft.utils.SendMessagesToCommandSender.sendBarMessage;
import static org.redfrog404.mobycraft.utils.SendMessagesToCommandSender.sendConfirmMessage;
import static org.redfrog404.mobycraft.utils.SendMessagesToCommandSender.sendErrorMessage;
import static org.redfrog404.mobycraft.utils.SendMessagesToCommandSender.sendFeedbackMessage;
import static org.redfrog404.mobycraft.utils.SendMessagesToCommandSender.sendMessage;

import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;

public class ImageCommands {
	
	public static void images() {
		sendFeedbackMessage("Loading...");

		DockerClient dockerClient = getDockerClient();

		List<Image> images = dockerClient.listImagesCmd().exec();

		if (images.size() == 0) {
			sendFeedbackMessage("No images currently installed.");
			return;
		}

		sendBarMessage(EnumChatFormatting.BLUE);
		sendMessage(EnumChatFormatting.AQUA + "Repository"
				+ EnumChatFormatting.RESET + ", " + EnumChatFormatting.GOLD
				+ "Tag" + EnumChatFormatting.RESET + ", "
				+ EnumChatFormatting.GREEN + "Size");
		sendBarMessage(EnumChatFormatting.BLUE);

		String tag = "";

		for (Image image : images) {
			String message = "";
			for (String name : image.getRepoTags()) {
				if (image.getRepoTags()[0].equals(name)) {
					message += EnumChatFormatting.AQUA + name.split(":")[0];
				} else {
					message += ", " + name.split(":")[0];
				}
				tag = name.split(":")[1];
			}
			message += EnumChatFormatting.RESET + ", "
					+ EnumChatFormatting.GOLD + tag + EnumChatFormatting.RESET
					+ ", " + EnumChatFormatting.GREEN
					+ convertBytesAndMultiply(image.getSize());
			sendMessage(message);
		}
	}

	public static void removeAllImages() {
		sendFeedbackMessage("Working on it...");
		if (getAll().size() < 1) {
			sendFeedbackMessage("No images currently currently.");
			return;
		}
		for (Image image : getImages()) {
			getDockerClient().removeImageCmd(image.getId()).exec();
		}
		sendConfirmMessage("Removed all images.");
	}

	public static void removeImage() {
		if (checkIfArgIsNull(2)) {
			sendErrorMessage("Container name not specified! Command is used as /docker rmi <name> .");
			return;
		}

		try {
			getDockerClient().removeImageCmd(getImageWithName(arg1).getId())
					.withForce().exec();
			sendConfirmMessage("Removed image with name \"" + arg1 + "\"");
		} catch (NullPointerException exception) {
			sendErrorMessage("No image exists with the name \"" + arg1 + "\"");
		}
	}

	public static List<Image> getImages() {
		return getDockerClient().listImagesCmd().exec();
	}

	public static Image getImageWithName(String name) {
		for (Image image : getImages()) {
			if (image.getRepoTags()[0].split(":")[0].equals(name)) {
				return image;
			}
		}
		return null;
	}

}
