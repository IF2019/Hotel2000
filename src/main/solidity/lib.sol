pragma solidity ^0.4.17;

library Lib {
	struct Booking {
		bool    isset;      // Should be true
		uint    id;
		uint    createdAt;  // Timestamp
		uint    start;      // Daystamp
		uint    end;        // Daystamp
		string  hotelCode;
		uint    room;       // Index
		uint    price;
		address client;
	}

	struct Room {
		bool isset; // Should be true
		mapping(uint => uint) bookings; // Daystamp => BookingID
	}

	struct Hotel {
		bool    isset;      // Should be true
		string  code;
		uint    createdAt;  // Timestamp
		string  title;
		string  description;
		uint    price;      // Price per night
		uint    nbRooms;
		address owner;
		mapping(uint => Room) rooms;
	}
}
