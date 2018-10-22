pragma solidity ^0.4.17;
import {Lib} from "./lib.sol";

contract Hotel2000 {

	uint32 bookingIdInc = 1;
	mapping(uint => Lib.Booking) bookings; // ResevationID => Reservation
	mapping(string => Lib.Hotel) hotels; // HotelCode  => Hotel

	constructor() public {}

	function initRoom(Lib.Room storage room) internal {
		room.isset = true;
	}

	function initHotel(Lib.Hotel storage hotel, string _code, uint32 _nbRooms, uint256 _price) internal {
		hotel.code      = _code;
		hotel.nbRooms   = _nbRooms;
		hotel.createdAt = now;
		hotel.price     = _price;
		for (uint32 i = 0; i < _nbRooms; i++) {
			initRoom(hotel.rooms[i]);
		}
		hotel.isset = true;
	}

	function canBuildHotel(string _code, uint32 _nbRooms, uint256 _price) public view returns(bool, string) {
		if (bytes(_code).length < 2 || bytes(_code).length > 8)
			return (false, "the code must be between 2 and 8 characters long");
		if (hotels[_code].isset) return (false, "this code already exists");
		if (_nbRooms < 1)     return (false, "the hotel must have more than 1 room");
		if (_nbRooms > 10000) return (false, "the hotel must have less than 10_000 rooms");
		if (_price   < 1)     return (false, "the rooms must cost more than 1");
		return (true, "");
	}

	function getHotel(string _code) public view returns(string, string, string, uint256, address, uint32) {
		require(hotels[_code].isset, "hotel not found");
		return(
			hotels[_code].code,
			hotels[_code].title,
			hotels[_code].description,
			hotels[_code].price,
			hotels[_code].owner,
			hotels[_code].nbRooms
		);
	}

	function buildHotel(string _code, uint32 _nbRooms, uint256 _price) public {
		bool          canBuild;
		string memory message;
		(canBuild, message) = canBuildHotel(_code, _nbRooms, _price);
		require(canBuild, message);
		initHotel(hotels[_code], _code, _nbRooms, _price);
	}

    // start and end are daystamps
    function canBook(string _code, uint32 _start, uint32 _end, uint32 _room) view public returns(bool, string) {
        bool          bookable;
        string memory message;
        (bookable, message) = canBook_internal(_code, _start, _end, _room);
        if (!bookable) return (bookable, message);
        if (msg.sender.balance < hotels[_code].price) return (false, "your balance isn't high enough");
        return (true, "you can book this room");
    }

    // start and end are daystamps
    function canBook_internal(string _code, uint32 _start, uint32 _end, uint32 _room) view internal returns(bool, string) {
        Lib.Hotel storage hotel = hotels[_code];
        require(hotel.isset, "hotel not found");
        Lib.Room storage room = hotels[_code].rooms[_room];
        require(room.isset, "room not found");
        if (_start >= _end) return (false, "the booking start must happen before the booking end");
        for (uint32 i = _start; i <= _end; i++) {
            // @IMPROVE add the booking days
            if (bookings[room.bookings[i]].isset) return (false, "the room has already been booked");
        }

        return (true, "");
    }
    
    // start and end are daystamps
    function book(string _code, uint32 _start, uint32 _end, uint32 _room) public payable {
        bool          bookable;
        string memory message;
        (bookable, message) = canBook_internal(_code, _start, _end, _room);
        require(bookable, message);

        Lib.Hotel   storage hotel = hotels[_code];
        Lib.Room    storage room  = hotel.rooms[_room];
        
        if (msg.value < hotel.price) {
            msg.sender.transfer(msg.value);
            return;
        }

        uint32 booking_id = bookingIdInc++;
        Lib.Booking storage booking = bookings[booking_id];
        
        booking.isset     = true;
        booking.client    = msg.sender;
        booking.hotelCode = hotel.code;
        booking.price     = hotel.price;
        booking.createdAt = now;
        booking.id        = booking_id;
        booking.start     = _start;
        booking.end       = _end;
        booking.room      = _room;
        
        // @TODO Add timeout events to refund or send money to the owner

        for (uint32 i = _start; i < _end; i++) {
            room.bookings[i] = booking_id;
        }
    }

	function timestampToDaystamp(uint256 timestamp) pure public returns(uint32) {
		return uint32(timestamp / 86400);
	}

	function test() public pure returns (string) {
		return "test success";
	}
}
