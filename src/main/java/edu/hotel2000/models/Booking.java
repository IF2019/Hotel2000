package edu.hotel2000.models;

import lombok.*;
import org.web3j.tuples.generated.Tuple8;

import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Booking{
	String clientAddress;
	String hotelCode;
	Money price;
	BigInteger createdAt;  // Timestamp
	int id;
	DateStamp start;
	DateStamp end;
	int roomId;

	public Booking(Tuple8<String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> data){
		clientAddress = data.getValue1();
		hotelCode = data.getValue2();
		price = Money.of(data.getValue3());
		createdAt = data.getValue4();
		id = data.getValue5().intValue();
		start = DateStamp.of(data.getValue6());
		end = DateStamp.of(data.getValue7());
		roomId = data.getValue8().intValue();
	}
}
