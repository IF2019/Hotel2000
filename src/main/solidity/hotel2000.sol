pragma solidity ^0.4.17;
import {Lib} from "./lib.sol";

contract Hotel2000 {

	uint bookingIdInc = 1;
	mapping(uint => Lib.Booking) bookings; // ResevationID => Reservation
	mapping(string => Lib.Hotel) hotels; // HotelCode  => Hotel

	constructor() public {}

	function initRoom(Lib.Room storage room) internal {
		room.isset = true;
	}

	function initHotel(Lib.Hotel storage hotel, string _code, uint _nbRooms, uint _price) internal {
		hotel.code      = _code;
		hotel.nbRooms   = _nbRooms;
		hotel.createdAt = now;
		hotel.price     = _price;
		for (uint i = 0; i < _nbRooms; i++) {
			initRoom(hotel.rooms[i]);
		}
		hotel.isset = true;
	}

	function canBuildHotel(string _code, uint _nbRooms, uint _price) public view returns(bool, string) {
		if (bytes(_code).length < 2 || bytes(_code).length > 8)
            return (false, "the code must be between 2 and 8 characters long");
		if (hotels[_code].isset) return (false, "this code already exists");
		if (_nbRooms < 1)     return (false, "the hotel must have more than 1 room");
		if (_nbRooms > 10000) return (false, "the hotel must have less than 10_000 rooms");
		if (_price   < 1)     return (false, "the rooms must cost more than 1");
		return (true, "");
	}

	function getHotel(string _code) public view returns(string, string, string, uint, address, uint) {
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

	function buildHotel(string _code, uint _nbRooms, uint _price) public {
		bool          canBuild;
		string memory message;
		(canBuild, message) = canBuildHotel(_code, _nbRooms, _price);
		require(canBuild, message);
		initHotel(hotels[_code], _code, _nbRooms, _price);
	}

	function timestampToDaystamp(uint timestamp) pure public returns(uint) {
		return timestamp / 86400;
	}

	function test() public pure returns (string) {
		return "test success";
	}
}
