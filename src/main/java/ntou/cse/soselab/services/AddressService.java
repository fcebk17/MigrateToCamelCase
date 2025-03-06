package ntou.cse.soselab.services;

import java.util.List;

import ntou.cse.soselab.entites.Address;
import ntou.cse.soselab.payloads.AddressDTO;

public interface AddressService {
	
	AddressDTO createAddress(AddressDTO addressDTO);
	
	List<AddressDTO> getAddresses();
	
	AddressDTO getAddress(Long addressId);
	
	AddressDTO updateAddress(Long addressId, Address address);
	
	String deleteAddress(Long addressId);
}
