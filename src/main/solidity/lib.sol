pragma solidity ^0.4.17;

library Lib{
	struct Resevation{
		bool isset; // Should by true
		uint id;
		uint createdAt; // Timestamp
		uint debut; // Daystamp
		uint fin; // Daystamp
		string hotelCode;
		uint chambre; // Index
		uint prix;
		address client;
	}

	struct Chambre{
		bool isset; // Should by true
		mapping(uint => uint) reservations; // Daystamp => ResevationID
	}

	struct Hotel{
		bool isset; // Should by true
		string code;
		uint createdAt; // Timestamp
		string title;
		string descitions;
		uint prix; // Prix par nuit
		mapping(uint => Chambre) chambres;
		uint nbChambres;
		address proprio;
	}
}