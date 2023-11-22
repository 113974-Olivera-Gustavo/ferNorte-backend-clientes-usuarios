package com.example.micro_b.services;

import com.example.micro_b.dtos.CargoRequest;
import com.example.micro_b.dtos.RegistroResponse;
import com.example.micro_b.dtos.TipoDocumentoRequest;
import com.example.micro_b.entities.CargoEntity;
import com.example.micro_b.entities.TipoDocumentoEntity;
import com.example.micro_b.entities.UsuarioEntity;
import com.example.micro_b.repositories.CargoRepository;
import com.example.micro_b.repositories.TipoDocumentoRepository;
import com.example.micro_b.repositories.UsuarioRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServices
{
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final CargoRepository cargoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    public UserServices(UsuarioRepository usuarioRepository, ModelMapper modelMapper, CargoRepository cargoRepository, TipoDocumentoRepository tipoDocumentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.cargoRepository = cargoRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    //url ejemplo postman: http://localhost:8081/auth/getUser/ByCargo?cargo=cliente
    public List<RegistroResponse> findUsersByCargo(String descripcion){
        List<UsuarioEntity> results = usuarioRepository.findByCargo_DescripcionIgnoreCase(descripcion);

        if (results.isEmpty()) {
            throw new RuntimeException("No se encontraron usuarios con el cargo especificado");
        }
        return results.stream()
                .map(result -> {
                    RegistroResponse response = new RegistroResponse();
                    response.setNombre(result.getNombre());
                    response.setApellido(result.getApellido());
                    response.setUsername(result.getUsername());
                    response.setPassword(result.getPassword());
                    response.setEmail(result.getEmail());
                    response.setTelefono(result.getTelefono());
                    response.setNumero_documento(result.getNumeroDocumento());
                    response.setActivo(result.getActivo());
                    response.setId_cargo(result.getCargo());
                    response.setId_tipo_documento(result.getId_tipo_documento());
                    return response;
                })
                .collect(Collectors.toList());
    }

    //url ejemplo postman: http://localhost:8081/auth/createCargo"
    //Definir body en CargoRequest
    public CargoEntity createCargo(CargoRequest cargoRequest){
        String descripcion = cargoRequest.getDescripcion().toLowerCase();

        Optional<CargoEntity> existingCargo = cargoRepository.findAll()
                .stream()
                .filter(cargo -> cargo.getDescripcion().toLowerCase().equals(descripcion))
                .findAny();

        if (existingCargo.isPresent()) {
            throw new RuntimeException("El cargo '" + cargoRequest.getDescripcion() + "' ya existe.");
        }

        CargoEntity cargoEntity = modelMapper.map(cargoRequest, CargoEntity.class);
        return cargoRepository.save(cargoEntity);
    }

    public TipoDocumentoEntity createTipoDoc(TipoDocumentoRequest tipoDocumentoRequest) {
        String nombre = tipoDocumentoRequest.getDescripcion().toLowerCase();

        Optional<TipoDocumentoEntity> existingTipoDoc = tipoDocumentoRepository.findAll()
                .stream()
                .filter(tipoDoc -> tipoDoc.getDescripcion().toLowerCase().equals(nombre))
                .findAny();

        if (existingTipoDoc.isPresent()) {
            throw new RuntimeException("El tipo de documento '" + tipoDocumentoRequest.getDescripcion() + "' ya existe.");
        }

        TipoDocumentoEntity tipoDocumentoEntity = modelMapper.map(tipoDocumentoRequest, TipoDocumentoEntity.class);
        return tipoDocumentoRepository.save(tipoDocumentoEntity);
    }


    //url ejemplo postman: http://localhost:8081/auth/baja-logica/"dni" <-- cambiar al dni a borrar
    public boolean bajaUser(String numeroDocumento) {
        Optional<UsuarioEntity> optionalUser = usuarioRepository.findByNumeroDocumento(numeroDocumento);

        if (optionalUser.isPresent()) {
            UsuarioEntity user = optionalUser.get();
            user.setActivo(false);
            usuarioRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public List<CargoRequest> getCargos() {
        List<CargoEntity> results = cargoRepository.findAll();

        if (results.isEmpty()) {
            throw new RuntimeException("No se encontraron cargos");
        }

        return results.stream()
                .map(result -> {
                    CargoRequest response = new CargoRequest();
                    response.setId_cargo(result.getId_cargo());
                    response.setDescripcion(result.getDescripcion());
                    return response;
                })
                .collect(Collectors.toList());
    }

}
