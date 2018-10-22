package edu.hotel2000.models;

import lombok.*;

import java.math.BigInteger;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Room{
	private Map<BigInteger, BigInteger> bookings; // Daystamp => BookingID
}
