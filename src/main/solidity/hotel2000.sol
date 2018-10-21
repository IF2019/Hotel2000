pragma solidity ^0.4.17;
import {Lib} from "./lib.sol";

contract Hotel2000{

	uint reservationIdInc = 1;
	mapping(uint => Lib.Resevation) reservations; // ResevationID => Reservation
	mapping(string => Lib.Hotel) hotels; // HotelCode  => Hotel

	constructor() public{}

	function initChambre(Lib.Chambre storage chambre)internal{
		chambre.isset = true;
	}

	function initHotel(Lib.Hotel storage hotel,string _code, uint _nbChambres, uint _prix)internal{
		hotel.code=_code;
		hotel.nbChambres=_nbChambres;
		hotel.createdAt=now;
		hotel.prix=_prix;
		for(uint i = 0; i<_nbChambres; i++){
			initChambre(hotel.chambres[i]);
		}
		hotel.isset=true;
	}

	function canBuildHotel(string _code, uint _nbChambres, uint _prix) public view returns(bool, string){
		if(bytes(_code).length < 2 || bytes(_code).length > 8) return (false, "le code doit contenir entre 2 et 8 caractÃ©re");
		if(hotels[_code].isset) return (false, "Code dÃ©ja Existant");
		if(_nbChambres < 1) return (false, "nbChambre < 1");
		if(_nbChambres > 10000) return (false, "trop de chambre!");
		if(_prix < 1) return (false, "prix < 1");
		return (true, "");
	}

	function getHotel(string _code) public view returns(string, string, string, uint, address, uint){
		require(hotels[_code].isset, "Hotel not found");
		return(
		hotels[_code].code,
		hotels[_code].title,
		hotels[_code].descitions,
		hotels[_code].prix,
		hotels[_code].proprio,
		hotels[_code].nbChambres
		);
	}

	function buildHotel(string _code, uint _nbChambres, uint _prix) public{
		bool canBuil;
		string memory message;
		(canBuil, message) = canBuildHotel(_code, _nbChambres, _prix);
		require(canBuil, message);
		initHotel(hotels[_code], _code, _nbChambres, _prix);

	}


	function timestampToDaystamp(uint timestamp) pure public returns(uint){
		return timestamp / 86400;
	}

	function test() public pure returns (string){
		return "Test success";
	}
}