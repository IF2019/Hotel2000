package edu.hotel2000.models;

import lombok.*;
import org.web3j.tuples.generated.Tuple9;

import java.math.BigInteger;
import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Hotel{

	private String owner;
	private Room[] rooms;
	private int[] activeBookingsId;
	private int[] bookingsId;
	private String code;
	private String title;
	private String description;
	private Money price;      // Price per night
	private BigInteger createdAt;  // Timestamp

	public Hotel(Tuple9<String, BigInteger, BigInteger, BigInteger, String, String, String, BigInteger, BigInteger> data){
		owner = data.getValue1();
		rooms = new Room[data.getValue2().intValue()];
		Arrays.setAll(rooms, i -> new Room());

		bookingsId = new int[data.getValue3().intValue()];
		activeBookingsId = new int[data.getValue4().intValue()];
		code = data.getValue5();
		title = data.getValue6();
		description = data.getValue7();
		price = Money.of(data.getValue8());
		createdAt = data.getValue9();
	}
}
