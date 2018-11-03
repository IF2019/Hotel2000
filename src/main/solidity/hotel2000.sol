pragma solidity ^0.4.17;
import {Lib} from "./lib.sol";

contract Hotel2000 {
	uint256 constant TIME_SCALE = 1440;

	uint32 bookingIdInc = 1;
	mapping(uint32    => Lib.Booking) bookings; // ResevationID => Reservation
	mapping(string  => Lib.Hotel) hotels; // HotelCode  => Hotel

	constructor() public {}

	function initRoom(Lib.Room storage room) internal {
		room.isset = true;
	}

	function initHotel(Lib.Hotel storage hotel, string _code, uint32 _nbRooms, uint256 _price) internal {
		hotel.code      = _code;
		hotel.createdAt = now;
		hotel.price     = _price;
		hotel.rooms.length = _nbRooms;
		for (uint32 i = 0; i < _nbRooms; i++) {
			initRoom(hotel.rooms[i]);
		}
		hotel.owner = msg.sender;
		hotel.isset = true;
	}

	function canCreateHotel(string _code, uint32 _nbRooms, uint256 _price) public view returns(bool, string) {
		if (bytes(_code).length < 2 || bytes(_code).length > 8)
			return (false, "the code must be between 2 and 8 characters long");
		if (hotels[_code].isset) return (false, "this code already exists");
		if (_nbRooms < 1)     return (false, "the hotel must have more than 1 room");
		if (_nbRooms > 10000) return (false, "the hotel must have less than 10_000 rooms");
		if (_price   < 1)     return (false, "the rooms must cost more than 1");
		return (true, "");
	}

	function getHotel(string _code) public view returns(address, uint32, uint32, uint32, string, string, string, uint256, uint256) {
		Lib.Hotel storage hotel = hotels[_code];
		require(hotel.isset, "hotel not found");
		return(
		hotel.owner,
		uint32(hotel.rooms.length),
		uint32(hotel.bookings.length),
		uint32(hotel.active_bookings.length),
		hotel.code,
		hotel.title,
		hotel.description,
		hotel.price,
		hotel.createdAt
		);
	}

//	function getHotelRoom(string _code, uint32 index) public view returns(DATA){
//		Lib.Hotel storage hotel = hotels[_code];
//		require(hotel.isset, "hotel not found");
//		require(hotel.rooms.length > index, "roomId not found");
//		Lib.Room storage roomId = hotel.rooms[index];
//		return (
//			DATA
//		);
//	}

	function getHotelRoomBookingId(string _code, uint32 index, uint256 timestamps) public view returns (uint32){
		Lib.Hotel storage hotel = hotels[_code];
		require(hotel.isset, "hotel not found");
		require(hotel.rooms.length > index, "room not found");
		Lib.Room storage room = hotel.rooms[index];
		return room.bookings[timestampToDaystamp(timestamps)];
	}


	function getHotelBookingId(string _code, uint32 index) public view returns(uint32){
		Lib.Hotel storage hotel = hotels[_code];
		require(hotel.isset, "hotel not found");
		require(hotel.bookings.length > index, "room not found");
		return(hotel.bookings[index]);
	}

	function getHotelActiveBookingId(string _code, uint32 index) public view returns(uint32){
		Lib.Hotel storage hotel = hotels[_code];
		require(hotel.isset, "hotel not found");
		require(hotel.active_bookings.length > index, "room not found");
		return(hotel.active_bookings[index]);
	}

	function getBooking(uint32 id) public view returns(address, string, uint256, uint256, uint32, uint32, uint32, uint32){
		Lib.Booking storage booking = bookings[id];
		require(booking.isset, "booking not found");
		return(
		booking.client,
		booking.hotelCode,
		booking.price,
		booking.createdAt,  // Timestamp
		booking.id,
		booking.start,      // Daystamp
		booking.end,        // Daystamp
		booking.room       // Index
		);
	}

	function getBookingPrice(string _code, uint256 _start_d, uint256 _end_d) view public returns(bool, uint256) {
		uint32 _start = timestampToDaystamp(_start_d);
		uint32 _end = timestampToDaystamp(_end_d);

		Lib.Hotel storage hotel = hotels[_code];
		if (!hotel.isset) return (false, 0);
		return (true, (_end - _start) * hotels[_code].price);
	}

	function createHotel(string _code, uint32 _nbRooms, uint256 _price) public {
		bool          canCreate;
		string memory message;
		(canCreate, message) = canCreateHotel(_code, _nbRooms, _price);
		require(canCreate, message);
		initHotel(hotels[_code], _code, _nbRooms, _price);
	}

	// start and end are timestamps
	function canBook(string _code, uint256 _start_d, uint256 _end_d, uint32 _room) view public returns(bool, string) {
		uint32 _start = timestampToDaystamp(_start_d);
		uint32 _end = timestampToDaystamp(_end_d);

		bool          bookable;
		string memory message;
		(bookable, message) = canBook_internal(_code, _start, _end, _room);
		if (!bookable) return (bookable, message);
		return (true, "you can book this room");
	}

	// start and end are timestamps        if (msg.sender.balance < hotels[_code].price) return (false, "your balance isn't high enough");

	function canBook_internal(string _code, uint32 _start, uint32 _end, uint32 _room) view internal returns(bool, string) {
		Lib.Hotel storage hotel = hotels[_code];
		require(hotel.isset, "hotel not found");
		Lib.Room storage room = hotels[_code].rooms[_room];
		require(room.isset, "room not found");
		if (_start >= _end) return (false, "the booking start must happen before the booking end");
		if (_start <= timestampToDaystamp(now)) return (false, "the booking start must happen after today");
		for (uint32 i = _start; i <= _end; i++) {
			// @IMPROVE add the booking days
			if (bookings[room.bookings[i]].isset) return (false, "the room has already been booked");
		}

		return (true, "");
	}

	// start and end are timestamps
	function book(string _code, uint256 _start_d, uint256 _end_d, uint32 _room) public payable {
		uint32 _start = timestampToDaystamp(_start_d);
		uint32 _end = timestampToDaystamp(_end_d);

		bool          bookable;
		string memory message;
		(bookable, message) = canBook_internal(_code, _start, _end, _room);
		require(bookable, message);

		Lib.Hotel   storage hotel = hotels[_code];
		Lib.Room    storage room  = hotel.rooms[_room];


		uint32 booking_id = bookingIdInc++;
		Lib.Booking storage booking = bookings[booking_id];

		hotel.active_bookings.length++;
		hotel.active_bookings[hotel.active_bookings.length-1] = booking_id;
		hotel.bookings.length++;
		hotel.bookings[hotel.bookings.length-1] = booking_id;

		booking.isset     = true;
		booking.client    = msg.sender;
		booking.hotelCode = hotel.code;
		booking.price     = hotel.price * (_end - _start);
		booking.createdAt = now;
		booking.id        = booking_id;
		booking.start     = _start;
		booking.end       = _end;
		booking.room      = _room;

		require(msg.value >= booking.price, "you must send enough money to pay for the booking");

		for (uint32 i = _start; i < _end; i++) {
			room.bookings[i] = booking_id;
		}
	}

	function withdraw(string _code) public {
		Lib.Hotel   storage hotel = hotels[_code];
		uint256             transfer = 0;

		require(msg.sender == hotel.owner, "you can only withdraw if you are the hotel owner");

		for (uint32 i = 0; i < hotel.active_bookings.length; ) {
			Lib.Booking storage booking = bookings[hotel.active_bookings[i]];
			if (booking.end < timestampToDaystamp(now)) {
				transfer += booking.price;
				hotel.active_bookings[i] = hotel.active_bookings[hotel.active_bookings.length-1];
				hotel.active_bookings.length--;
			} else {
				i++;
			}
		}

		msg.sender.transfer(transfer);
	}

	function editDescription(string _code, string _description) public {
		Lib.Hotel storage hotel = hotels[_code];
		require(msg.sender == hotel.owner, "not owner");
		hotel.description = _description;
	}

	function timestampToDaystamp(uint256 timestamp) pure public returns(uint32) {
		return uint32(timestamp / (86400 / TIME_SCALE));
	}

	function test() public pure returns (string) {
		return "test success";
	}

//	function test() public view returns (string, uint, uint, uint) {
//		Lib.Hotel   storage hotel = hotels["Test"];
//		uint256             transfer = 0;
//		uint32[]           test;
//		test.length = hotel.active_bookings.length;
//		for (uint32 j = 0; j < test.length; j++) test[j] = hotel.active_bookings[i];
//
//		if(!hotel.isset) return ("Hotel Test not found", 0,0,0);
//
//		for (uint32 i = 0; i < test.length; ) {
//			Lib.Booking memory booking;
//			booking = bookings[hotel.active_bookings[i]];
//			if (booking.end < timestampToDaystamp(now)) {
//				transfer += booking.price;
//				hotel.active_bookings[i] = hotel.active_bookings[--test.length];
//			} else {
//				i++;
//				return("nop", booking.end, now, timestampToDaystamp(now));
//			}
//		}
//		return("finish", test.length, transfer, 0);
//	}
}
