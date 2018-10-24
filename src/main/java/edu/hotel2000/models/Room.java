package edu.hotel2000.models;

import lombok.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Room{
	private Map<Long, Integer> booking = new HashMap<>(); // Daystamp => BookingID
}
