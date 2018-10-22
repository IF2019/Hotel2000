package edu.hotel2000.models;

import lombok.*;
import org.web3j.protocol.Web3j;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConsoleEnv{

	private Web3j web3j;
	private String contractAddress;
	private String accountName;

}
