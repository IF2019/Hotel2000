package edu.hotel2000.models;

import lombok.*;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ConsoleEnv{

	private Web3j web3j;
	private String contractAddress;
	private String accountName;
	private StaticGasProvider gasProvider;

}
