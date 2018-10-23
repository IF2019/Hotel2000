package edu.hotel2000.models;

import lombok.*;
import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Hotel{

	private String owner;
	private Map<BigInteger, Room> rooms;
	private String code;
	private String title;
	private String description;
	private BigInteger price;      // Price per night
	private BigInteger createdAt;  // Timestamp
	private BigInteger nbRooms;

	public Hotel(Tuple6<String, String, String, BigInteger, String, BigInteger> data){
		code = data.getValue1();
		title = data.getValue2();
		description = data.getValue3();
		price = data.getValue4();
		owner = data.getValue5();
		nbRooms = data.getValue6();
	}
}
