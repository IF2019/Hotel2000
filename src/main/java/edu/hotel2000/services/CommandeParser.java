package edu.hotel2000.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CommandeParser{

	private enum CommandElementType{
		TEXT("^(([^<>\\[\\]\\(\\)]*))$"),
		REQUIRED_VALUE("^\\<(([^<>\\[\\]\\(\\)]*))\\>$"),
		DEFAULT_VALUE("^\\[([^<>\\[\\]\\(\\)]*)\\]=([^<>\\[\\]\\(\\)]*)$"),
		OPTIONAL_VALUE("^\\[(([^<>\\[\\]\\(\\)]*))\\]$");

		private final Pattern pattern;

		CommandElementType(String patternS){
			pattern = Pattern.compile(patternS);
		}
	}

	private class CommandElement{
		String name;
		String defaultValue;
		CommandElementType type;

		CommandElement(String s){

			for(CommandElementType type : CommandElementType.values()){
				Matcher matcher = type.pattern.matcher(s);
				if(matcher.matches()){
					name = matcher.group(1);
					defaultValue = matcher.group(2);
					this.type = type;
					break;
				}
			}
		}
	}

	private class CommandeFormat{
		private final CommandElement[] commandElements;

		CommandeFormat(String format){
			commandElements = Arrays.asList(format.split(" "))
					.stream()
					.map(CommandElement::new)
					.toArray(CommandElement[]::new);
		}
	}

	public Optional<Map<String, String>> parse(String commande[], CommandeFormat format, int indexC, int indexF){
		if(commande.length == indexC && format.commandElements.length == indexF)
			return Optional.of(new HashMap<>());
		if(commande.length < indexC || format.commandElements.length <= indexF) return Optional.empty();

		CommandElement commandElement = format.commandElements[indexF];

		Optional<Map<String, String>> res;
		switch(commandElement.type){
			case TEXT:
				if(commande.length <= indexC) return Optional.empty();
				for(String testedValue : commandElement.name.split("\\|")){
					if(testedValue.equalsIgnoreCase(commande[indexC]))
						return parse(commande, format, indexC + 1, indexF + 1);
				}
				return Optional.empty();
			case REQUIRED_VALUE:
				res = parse(commande, format, indexC + 1, indexF + 1);
				res.ifPresent(stringStringMap -> stringStringMap.put(commandElement.name, commande[indexC]));
				return res;
			case DEFAULT_VALUE:
			case OPTIONAL_VALUE:
				res = parse(commande, format, indexC + 1, indexF + 1);
				if(res.isPresent()){
					res.get().put(commandElement.name, commande[indexC]);
					return res;
				}
				res = parse(commande, format, indexC, indexF+1);
				res.ifPresent(stringStringMap -> {
					if(commandElement.type == CommandElementType.DEFAULT_VALUE)
						stringStringMap.put(commandElement.name, commandElement.defaultValue);
				});
				return res;
			default:
				return Optional.empty();
		}
	}

	private Optional<Map<String, String>> parse(String commande[], CommandeFormat format){
		return parse(commande,format,0,0);
	}

	private static Map<String,CommandeFormat> formaters = new HashMap<>();

	public Optional<Map<String, String>> parse(String commande[], String format){
		CommandeFormat formater = formaters.get(format);
		if(formater == null){
			formater = new CommandeFormat(format);
			formaters.put(format, formater);
		}
		return parse(commande, formater);

	}
}
