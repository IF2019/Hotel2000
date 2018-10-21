package edu.hotel2000;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.util.Optional;

public class AccountService{
	public static Credentials findFromConfig(String name) throws IOException, CipherException{
//		String deployerAddress = Config.get("hotel2000.account." + name + ".address");
		String deployerPassphrase = Config.get("hotel2000.account." + name + ".passphrase");
		String deployerFile = Config.get("hotel2000.account." + name + ".path");
		return WalletUtils.loadCredentials(deployerPassphrase, deployerFile);
	}
	public static Optional<Credentials> findFromConfigOption(String name){
		try{
			return Optional.of(findFromConfig(name));
		}catch(Exception e){
			return Optional.empty();
		}
	}
}
