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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServicesTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;
    @InjectMocks
    private UserServices userServices;

    private CargoEntity cargoEntity;
    private CargoRequest cargoRequest;
    private UsuarioEntity usuarioEntity;


    @BeforeEach
    public void setUp() {
        cargoEntity = new CargoEntity();
        cargoEntity.setId_cargo(1L);
        cargoEntity.setDescripcion("Test Cargo");

        cargoRequest = new CargoRequest();
        cargoRequest.setId_cargo(1L);
        cargoRequest.setDescripcion("Test Cargo");

        usuarioEntity = new UsuarioEntity();
        usuarioEntity.setNumeroDocumento("12345678");
        usuarioEntity.setActivo(true);
    }

    @Test
    public void testGetCargosWhenCargosFoundThenReturnCargos() {
        when(cargoRepository.findAll()).thenReturn(Arrays.asList(cargoEntity));

        List<CargoRequest> result = userServices.getCargos();

        assertEquals(1, result.size());
        assertEquals(cargoRequest, result.get(0));
    }

    @Test
    public void testGetCargosWhenNoCargosFoundThenThrowException() {
        when(cargoRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> userServices.getCargos());
    }
    @Test
    public void testBajaUserWhenUserDoesNotExistThenReturnFalse() {
        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.empty());

        boolean result = userServices.bajaUser("12345678");

        assertFalse(result);
        verify(usuarioRepository, times(0)).save(any(UsuarioEntity.class));
    }
    @Test
    public void testBajaUserWhenUserExistsThenReturnTrue() {
        when(usuarioRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(usuarioEntity));

        boolean result = userServices.bajaUser("12345678");

        assertTrue(result);
        assertFalse(usuarioEntity.getActivo());
        verify(usuarioRepository, times(1)).save(usuarioEntity);
    }
    @Test
    public void testCreateCargoWhenCargoDoesNotExistThenReturnCargo() {
        when(cargoRepository.findAll()).thenReturn(Collections.emptyList());
        when(cargoRepository.save(cargoEntity)).thenReturn(cargoEntity);
        when(modelMapper.map(cargoRequest, CargoEntity.class)).thenReturn(cargoEntity);

        CargoEntity result = userServices.createCargo(cargoRequest);

        assertEquals(cargoEntity, result);
        verify(cargoRepository, times(1)).save(cargoEntity);
    }
    @Test
    public void testCreateCargoWhenCargoExistsThenThrowException() {
        when(cargoRepository.findAll()).thenReturn(Arrays.asList(cargoEntity));

        assertThrows(RuntimeException.class, () -> userServices.createCargo(cargoRequest));
        verify(cargoRepository, times(0)).save(any(CargoEntity.class));
    }
    @Test
    public void testCreateTipoDocWhenTipoDocDoesNotExistThenReturnTipoDoc() {
        TipoDocumentoRequest tipoDocumentoRequest = new TipoDocumentoRequest();
        tipoDocumentoRequest.setId_tipo_documento(1L);
        tipoDocumentoRequest.setDescripcion("Test Tipo Documento");

        TipoDocumentoEntity tipoDocumentoEntity = new TipoDocumentoEntity();
        tipoDocumentoEntity.setId_tipo_documento(1L);
        tipoDocumentoEntity.setDescripcion("Test Tipo Documento");

        when(tipoDocumentoRepository.findAll()).thenReturn(Collections.emptyList());
        when(tipoDocumentoRepository.save(tipoDocumentoEntity)).thenReturn(tipoDocumentoEntity);
        when(modelMapper.map(tipoDocumentoRequest, TipoDocumentoEntity.class)).thenReturn(tipoDocumentoEntity);

        TipoDocumentoEntity result = userServices.createTipoDoc(tipoDocumentoRequest);

        assertEquals(tipoDocumentoEntity, result);
        verify(tipoDocumentoRepository, times(1)).save(tipoDocumentoEntity);
    }

    @Test
    public void testCreateTipoDocWhenTipoDocExistsThenThrowException() {
        TipoDocumentoRequest tipoDocumentoRequest = new TipoDocumentoRequest();
        tipoDocumentoRequest.setId_tipo_documento(1L);
        tipoDocumentoRequest.setDescripcion("Test Tipo Documento");

        TipoDocumentoEntity tipoDocumentoEntity = new TipoDocumentoEntity();
        tipoDocumentoEntity.setId_tipo_documento(1L);
        tipoDocumentoEntity.setDescripcion("Test Tipo Documento");

        when(tipoDocumentoRepository.findAll()).thenReturn(Arrays.asList(tipoDocumentoEntity));

        assertThrows(RuntimeException.class, () -> userServices.createTipoDoc(tipoDocumentoRequest));
        verify(tipoDocumentoRepository, times(0)).save(any(TipoDocumentoEntity.class));
    }
    @Test
    public void testFindUsersByCargoWhenNoUsersExistThenThrowException() {
        String descripcion = "Vendedor";
        List<UsuarioEntity> usuarioEntities = Collections.emptyList();

        when(usuarioRepository.findByCargo_DescripcionIgnoreCase(descripcion)).thenReturn(usuarioEntities);

        assertThrows(RuntimeException.class, () -> userServices.findUsersByCargo(descripcion));
    }

}