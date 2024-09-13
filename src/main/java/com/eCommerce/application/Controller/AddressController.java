package com.eCommerce.application.Controller;

import com.eCommerce.application.Entity.User;
import com.eCommerce.application.Model.AddressDTO;
import com.eCommerce.application.Service.AddressService;
import com.eCommerce.application.Util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO){
        User user = authUtils.getLogedInUser();
        AddressDTO savedAddress = addressService.addAddressToCurrentUser(addressDTO, user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses(){
        List<AddressDTO> addresses= addressService.getAllAddresses();
        return new ResponseEntity<>(addresses,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO addresses= addressService.getAddressById(addressId);
        return new ResponseEntity<>(addresses,HttpStatus.OK);
    }

    @GetMapping("/addresses/user")
    public ResponseEntity<List<AddressDTO>> getAddressByUser(){
        User user = authUtils.getLogedInUser();
        List<AddressDTO> savedAddress = addressService.getAddressByUser(user);
        return new ResponseEntity<>(savedAddress, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO){
        AddressDTO savedAddressDTO = addressService.updateAddressById(addressId, addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId){
        String result = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
