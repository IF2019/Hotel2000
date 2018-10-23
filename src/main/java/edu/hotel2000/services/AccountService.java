package edu.hotel2000.services;

import edu.hotel2000.Config;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class AccountService{
	public Credentials findFromConfig(String name) throws IOException, CipherException{
//		String deployerAddress = Config.get("hotel2000.account." + name + ".address");
		String deployerPassphrase = Config.get("hotel2000.account." + name + ".passphrase");
		String deployerFile = Config.get("hotel2000.account." + name + ".path");
		return WalletUtils.loadCredentials(deployerPassphrase, deployerFile);
	}
	public Optional<Credentials> findFromConfigOption(String name) throws IOException, CipherException{
		try{
			return Optional.of(findFromConfig(name));
		}catch(NullPointerException e){
			return Optional.empty();
		}
	}

	public Optional<String> getAddress(String name){
		return Optional.ofNullable(Config.get("hotel2000.account." + name + ".address"));
	}
}
