package com.eCommerce.application.Service;

import com.eCommerce.application.Entity.User;
import com.eCommerce.application.Model.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO addAddressToCurrentUser(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAddressByUser(User user);

    AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO);

    String deleteAddressById(Long addressId);
}
