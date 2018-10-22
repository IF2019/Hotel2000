pragma solidity ^0.4.17;

library Lib {
	struct Booking {
		address client;
		string  hotelCode;
		uint256 price;
		uint256 createdAt;  // Timestamp
		uint32  id;
		uint32  start;      // Daystamp
		uint32  end;        // Daystamp
		uint32  room;       // Index
		bool    isset;      // Should be true
	}

	struct Room {
		bool isset; // Should be true
		mapping(uint32 => uint32) bookings; // Daystamp => BookingID
	}

	struct Hotel {
		address owner;
		mapping(uint32 => Room) rooms;
		string  code;
		string  title;
		string  description;
		uint256 price;      // Price per night
		uint256 createdAt;  // Timestamp
		uint32  nbRooms;
		bool    isset;      // Should be true
	}
}
