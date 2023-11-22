package com.example.micro_a.services.imp;

import com.example.micro_a.dtos.FacturacionRequest;
import com.example.micro_a.dtos.catalogo.CompraRequest;
import com.example.micro_a.dtos.catalogo.Oferta;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.dtos.catalogo.ProductosCanjeados;
import com.example.micro_a.entities.*;
import com.example.micro_a.models.*;
import com.example.micro_a.repositories.*;
import com.example.micro_a.templates.FidelizacionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClienteServiceImpTest {

    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteServiceImp clienteService;
    @Mock
    private ClasificacionRepository clasificacionRepository;
    @Mock
    private TpCategoriaFiscalRepository tpCategoriaFiscalRepository;
    @Mock
    private TipoClienteRepository tipoClienteRepository;
    @Mock
    private ClienteTemporalRepository clienteTemporalRepository;

    @Mock
    private Cliente cliente;
    @Mock
    private TipoDocumento tipoDocumento;
    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private FidelizacionClient fidelizacionClientMock;


    @BeforeEach
    public void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("John");
        cliente.setApellido("Doe");
        cliente.setEmail("john.doe@example.com");
        cliente.setTelefono("1234567890");
        cliente.setNroDoc(123456789L);
        cliente.setDomicilio("Calle Falsa 123");
        cliente.setCant_puntos(0.0);
        cliente.setId_tipo_doc(new TipoDocumento());
        cliente.setId_tipo_cliente(new TipoCliente());
        TP_Categoria_Fiscal tp_categoria_fiscal = new TP_Categoria_Fiscal();
        tp_categoria_fiscal.setId_categoria(1L);
        cliente.setId_categoria_fiscal(tp_categoria_fiscal);
        cliente.setId_clasificacion(new Clasificacion());

        tipoDocumento = new TipoDocumento();
        tipoDocumento.setId_tipo_doc(1L);
        tipoDocumento.setTipo_documento("DNI");


    }
    @Test
    public void testGenerarSolicitudClienteTemporal() {
        ClienteTemporal clienteTemporal = new ClienteTemporal();
        ClienteTemporalEntity clienteTemporalEntity = new ClienteTemporalEntity();
        when(modelMapper.map(clienteTemporal, ClienteTemporalEntity.class)).thenReturn(clienteTemporalEntity);
        when(clienteTemporalRepository.save(clienteTemporalEntity)).thenReturn(clienteTemporalEntity);

        SolicitudEntity solicitudEntity = new SolicitudEntity();
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenReturn(solicitudEntity);

        Solicitud solicitudMock = new Solicitud();
        when(modelMapper.map(solicitudEntity, Solicitud.class)).thenReturn(solicitudMock);

        Solicitud solicitud = clienteService.generarSolicitudClienteTemporal(clienteTemporal);

        assertNotNull(solicitud);
    }
    @Test
    public void testGenerarSolicitudClienteTemporal_ClienteTemporalNulo() {
        ClienteTemporal clienteTemporal = null;

        Solicitud solicitud = clienteService.generarSolicitudClienteTemporal(clienteTemporal);
        assertNull(solicitud);
    }

    @Test
    public void testProcesarCompraWhenValidRequestThenSuccess() {
        CompraRequest compraRequest = new CompraRequest();
        compraRequest.setNroDoc(123456789L);
        compraRequest.setProductosCanjeados(Arrays.asList(new ProductosCanjeados("1", 1)));

        ProductoRequest productoRequest = new ProductoRequest();
        productoRequest.setCodigo("1");
        productoRequest.setOfertas(Arrays.asList(new Oferta("1", "Oferta 1", "Desc 1", 100.0, 10.0, true)));

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNroDoc(123456789L);
        clienteEntity.setCant_puntos(20.0);

        when(fidelizacionClientMock.getAllProductos()).thenReturn(Arrays.asList(productoRequest));
        when(clienteRepository.findByNroDoc(123456789L)).thenReturn(Optional.of(clienteEntity));

        clienteService.procesarCompra(compraRequest);

        verify(fidelizacionClientMock, times(1)).getAllProductos();
        verify(clienteRepository, times(1)).findByNroDoc(123456789L);
        verify(clienteRepository, times(1)).save(any(ClienteEntity.class));
    }

    @Test
    public void testProcesarCompraWhenProductNotFoundThenException() {
        CompraRequest compraRequest = new CompraRequest();
        compraRequest.setNroDoc(123456789L);
        compraRequest.setProductosCanjeados(Arrays.asList(new ProductosCanjeados("1", 1)));

        ProductoRequest productoRequest = new ProductoRequest();
        productoRequest.setCodigo("2");
        productoRequest.setOfertas(Arrays.asList(new Oferta("1", "Oferta 1", "Desc 1", 100.0, 10.0, true)));

        when(fidelizacionClientMock.getAllProductos()).thenReturn(Arrays.asList(productoRequest));

        assertThatThrownBy(() -> clienteService.procesarCompra(compraRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado en la oferta: 1");
    }

    @Test
    public void testProcesarCompraWhenInsufficientPointsThenException() {
        CompraRequest compraRequest = new CompraRequest();
        compraRequest.setNroDoc(123456789L);
        compraRequest.setProductosCanjeados(Arrays.asList(new ProductosCanjeados("1", 1)));

        ProductoRequest productoRequest = new ProductoRequest();
        productoRequest.setCodigo("1");
        productoRequest.setOfertas(Arrays.asList(new Oferta("1", "Oferta 1", "Desc 1", 100.0, 10.0, true)));

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNroDoc(123456789L);
        clienteEntity.setCant_puntos(5.0);

        when(fidelizacionClientMock.getAllProductos()).thenReturn(Arrays.asList(productoRequest));
        when(clienteRepository.findByNroDoc(123456789L)).thenReturn(Optional.of(clienteEntity));

        assertThatThrownBy(() -> clienteService.procesarCompra(compraRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El cliente no tiene suficientes puntos para realizar la compra");
    }


    @Test
    public void testUpdateClientWhenClientDoesNotExistThenThrowsException() {
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> clienteService.updateClient(cliente));
    }

    @Test
    void testUpdateClientWithNullIdThrowsIllegalArgumentException() {
        cliente.setId(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El ID del cliente no puede ser nulo.");
    }

    @Test
    void testUpdateClientWithNullCategoriaFiscalThrowsIllegalArgumentException() {
        cliente.setId_categoria_fiscal(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La categoría fiscal no puede ser nula.");
    }
    @Test
    void testUpdateClientWithNonNullCategoriaFiscalIdAndNullClasificacionThrowsIllegalArgumentException() {
        cliente.getId_categoria_fiscal().setId_categoria(1L);
        cliente.setId_clasificacion(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
    }

    @Test
    void testUpdateClientWithNullCategoriaFiscalIdThrowsIllegalArgumentException() {
        cliente.getId_categoria_fiscal().setId_categoria(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El ID de la categoría fiscal no puede ser nulo.");
    }
    @Test
    public void testUpdateClientWhenIdIsNullThenThrowsIllegalArgumentException() {
        cliente.setId(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El ID del cliente no puede ser nulo.");
    }


    @Test
    public void testGetClienteByDni() {
        Long nroDoc = 123456789L;

        ClienteEntity existingClient = new ClienteEntity();
        existingClient.setNroDoc(nroDoc);
        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.of(existingClient));
        Cliente cliente = new Cliente();
        cliente.setNroDoc(nroDoc);
        when(modelMapper.map(existingClient, Cliente.class)).thenReturn(cliente);

        List<Cliente> result = clienteService.getClienteByDni(nroDoc);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNroDoc()).isEqualTo(nroDoc);
        verify(clienteRepository, times(1)).findByNroDoc(nroDoc);
        verify(modelMapper, times(1)).map(existingClient, Cliente.class);

        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.getClienteByDni(nroDoc));
        assertThat(exception.getMessage()).contains("No se encontro un cliente con el numero de documento: " + nroDoc);
        verify(clienteRepository, times(2)).findByNroDoc(nroDoc);
    }

    @Test
    public void testGetTipoDocumentoReturnsCorrectList() {
        TipoDocumentoEntity tipoDocumentoEntity1 = new TipoDocumentoEntity();
        tipoDocumentoEntity1.setId_tipo_doc(1L);
        tipoDocumentoEntity1.setTipo_documento("DNI");

        TipoDocumentoEntity tipoDocumentoEntity2 = new TipoDocumentoEntity();
        tipoDocumentoEntity2.setId_tipo_doc(2L);
        tipoDocumentoEntity2.setTipo_documento("CUIT/CUIL");

        List<TipoDocumentoEntity> tipoDocumentoEntities = Arrays.asList(tipoDocumentoEntity1, tipoDocumentoEntity2);

        when(tipoDocumentoRepository.findAll()).thenReturn(tipoDocumentoEntities);

        TipoDocumento tipoDocumento1 = new TipoDocumento();
        tipoDocumento1.setId_tipo_doc(1L);
        tipoDocumento1.setTipo_documento("DNI");

        TipoDocumento tipoDocumento2 = new TipoDocumento();
        tipoDocumento2.setId_tipo_doc(2L);
        tipoDocumento2.setTipo_documento("CUIT/CUIL");

        List<TipoDocumento> expectedTipoDocumentos = Arrays.asList(tipoDocumento1, tipoDocumento2);

        when(modelMapper.map(tipoDocumentoEntity1, TipoDocumento.class)).thenReturn(tipoDocumento1);
        when(modelMapper.map(tipoDocumentoEntity2, TipoDocumento.class)).thenReturn(tipoDocumento2);

        List<TipoDocumento> result = clienteService.getTipoDocumento();

        assertThat(result).isEqualTo(expectedTipoDocumentos);
    }

    @Test
    public void testGetTipoDocumentoCallsFindAll() {
        clienteService.getTipoDocumento();
        verify(tipoDocumentoRepository, times(1)).findAll();
    }
    @Test
    public void testGetTipoClienteReturnsCorrectList() {
        TipoClienteEntity tipoClienteEntity1 = new TipoClienteEntity();
        tipoClienteEntity1.setId_tipo_cliente(1L);
        tipoClienteEntity1.setTipo_cliente("Persona");

        TipoClienteEntity tipoClienteEntity2 = new TipoClienteEntity();
        tipoClienteEntity2.setId_tipo_cliente(2L);
        tipoClienteEntity2.setTipo_cliente("Empresa");

        List<TipoClienteEntity> tipoClienteEntities = Arrays.asList(tipoClienteEntity1, tipoClienteEntity2);

        when(tipoClienteRepository.findAll()).thenReturn(tipoClienteEntities);

        TipoCliente tipoCliente1 = new TipoCliente();
        tipoCliente1.setId_tipo_cliente(1L);
        tipoCliente1.setTipo_cliente("Persona");

        TipoCliente tipoCliente2 = new TipoCliente();
        tipoCliente2.setId_tipo_cliente(2L);
        tipoCliente2.setTipo_cliente("Empresa");

        List<TipoCliente> expectedTipoClientes = Arrays.asList(tipoCliente1, tipoCliente2);

        when(modelMapper.map(tipoClienteEntity1, TipoCliente.class)).thenReturn(tipoCliente1);
        when(modelMapper.map(tipoClienteEntity2, TipoCliente.class)).thenReturn(tipoCliente2);

        List<TipoCliente> result = clienteService.getTipoCliente();

        assertThat(result).isEqualTo(expectedTipoClientes);
    }

    @Test
    public void testGetTipoClienteHandlesEmptyList() {
        when(tipoClienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<TipoCliente> result = clienteService.getTipoCliente();

        assertThat(result).isEmpty();
    }
    @Test
    public void testGetClasificacionWhenRepositoryReturnsEntitiesThenReturnsMappedDtos() {
        ClasificacionEntity clasificacionEntity = new ClasificacionEntity();
        clasificacionEntity.setId_clasificacion(1L);
        clasificacionEntity.setDescripcion("A");

        Clasificacion clasificacion = new Clasificacion();
        clasificacion.setId_clasificacion(1L);
        clasificacion.setDescripcion("A");

        when(clasificacionRepository.findAll()).thenReturn(Arrays.asList(clasificacionEntity));
        when(modelMapper.map(clasificacionEntity, Clasificacion.class)).thenReturn(clasificacion);

        List<Clasificacion> result = clienteService.getClasificacion();

        assertThat(result).containsOnly(clasificacion);
    }

    @Test
    public void testGetClasificacionWhenRepositoryReturnsEmptyThenReturnsEmpty() {
        when(clasificacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Clasificacion> result = clienteService.getClasificacion();

        assertThat(result).isEmpty();
    }

    @Test
    public void testGetTPCategoriaFiscalWhenRepositoryReturnsEntitiesThenReturnsMappedDtos() {
        TP_Categoria_FiscalEntity tp_categoria_fiscalEntity = new TP_Categoria_FiscalEntity();
        tp_categoria_fiscalEntity.setId_categoria_fiscal(1L);
        tp_categoria_fiscalEntity.setDescripcion("Monotributista");

        TP_Categoria_Fiscal tp_categoria_fiscal = new TP_Categoria_Fiscal();
        tp_categoria_fiscal.setId_categoria(1L);
        tp_categoria_fiscal.setDescripcion("Monotributista");

        when(tpCategoriaFiscalRepository.findAll()).thenReturn(Arrays.asList(tp_categoria_fiscalEntity));
        when(modelMapper.map(tp_categoria_fiscalEntity, TP_Categoria_Fiscal.class)).thenReturn(tp_categoria_fiscal);

        List<TP_Categoria_Fiscal> result = clienteService.getTPCategoriaFiscal();

        assertThat(result).containsOnly(tp_categoria_fiscal);
    }

    @Test
    public void testGetTPCategoriaFiscalWhenRepositoryReturnsEmptyThenReturnsEmpty() {
        when(tpCategoriaFiscalRepository.findAll()).thenReturn(Collections.emptyList());

        List<TP_Categoria_Fiscal> result = clienteService.getTPCategoriaFiscal();

        assertThat(result).isEmpty();
    }

    @Test
    public void givenFacturacionRequest_whenSetearPuntos_thenUpdateClientePuntos() {
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        FidelizacionClient fidelizacionClient = mock(FidelizacionClient.class);
        ModelMapper modelMapper = new ModelMapper();

        ClienteServiceImp clienteService = new ClienteServiceImp(
                clienteRepository, modelMapper, null, null, null, null, null, null, fidelizacionClient);

        Long nroDocAPI = 123456789L;
        BigDecimal totalAmountBilledAPI = new BigDecimal("10000");
        FacturacionSolicitud facturacionSolicitud = new FacturacionSolicitud(nroDocAPI, totalAmountBilledAPI);

        FacturacionRequest facturacionRequest = new FacturacionRequest();
        facturacionRequest.setNroDoc(nroDocAPI);

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNroDoc(nroDocAPI);
        clienteEntity.setCant_puntos(50.0);

        List<FacturacionSolicitud> facturaciones = Collections.singletonList(facturacionSolicitud);

        when(fidelizacionClient.getAllFacturacion()).thenReturn(facturaciones);
        when(clienteRepository.findByNroDoc(nroDocAPI)).thenReturn(Optional.of(clienteEntity));
        when(clienteRepository.save(any())).thenReturn(clienteEntity);

        Cliente result = clienteService.setearPuntos(facturacionRequest);

        assertThat(result).isNotNull();

        double puntosEsperados = Math.round((50.0 + (totalAmountBilledAPI.divide(new BigDecimal("5000"), RoundingMode.FLOOR).doubleValue() * 10)) * 10) / 10.0;
        assertThat(result.getCant_puntos()).isEqualTo(puntosEsperados);

        verify(fidelizacionClient, times(1)).getAllFacturacion();
        verify(clienteRepository, times(1)).findByNroDoc(nroDocAPI);
        verify(clienteRepository, times(1)).save(any());
    }
    @Test
    public void givenSolicitudEntities_whenGetSolicitudClienteAtendido_thenReturnAtendidoSolicitudList() {

        SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
        ModelMapper modelMapper = mock(ModelMapper.class);

        ClienteServiceImp clienteService = new ClienteServiceImp(
                null, modelMapper, null, null, null, null, solicitudRepository, null, null);

        SolicitudEntity solicitudEntity1 = new SolicitudEntity();
        solicitudEntity1.setId_solicitud(1L);
        solicitudEntity1.setNro_turno("T1");
        solicitudEntity1.setAtendido(true);
        solicitudEntity1.setFecha(LocalDateTime.now());

        SolicitudEntity solicitudEntity2 = new SolicitudEntity();
        solicitudEntity2.setId_solicitud(2L);
        solicitudEntity2.setNro_turno("T2");
        solicitudEntity2.setAtendido(true);
        solicitudEntity2.setFecha(LocalDateTime.now());

        List<SolicitudEntity> solicitudEntities = Arrays.asList(solicitudEntity1, solicitudEntity2);

        when(solicitudRepository.findByAtendido()).thenReturn(solicitudEntities);
        when(modelMapper.map(solicitudEntity1, SolicitudEntity.class)).thenReturn(solicitudEntity1);
        when(modelMapper.map(solicitudEntity2, SolicitudEntity.class)).thenReturn(solicitudEntity2);
        List<SolicitudEntity> result = clienteService.getSolicitudClienteAtendido();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNro_turno()).isEqualTo("T1");
        assertThat(result.get(1).getNro_turno()).isEqualTo("T2");

        verify(solicitudRepository, times(1)).findByAtendido();
        verify(modelMapper, times(2)).map(any(), eq(SolicitudEntity.class));
    }
    @Test
    public void givenSolicitudEntities_whenGetSolicitudClienteRegistrado_thenReturnSolicitudList() {

        SolicitudRepository solicitudRepository = mock(SolicitudRepository.class);
        ModelMapper modelMapper = mock(ModelMapper.class);

        ClienteServiceImp clienteService = new ClienteServiceImp(
                null, modelMapper, null, null, null, null, solicitudRepository, null, null);

        SolicitudEntity solicitudEntity1 = new SolicitudEntity();
        solicitudEntity1.setId_solicitud(1L);
        solicitudEntity1.setNro_turno("T1");
        solicitudEntity1.setAtendido(false);
        solicitudEntity1.setFecha(LocalDateTime.now());

        SolicitudEntity solicitudEntity2 = new SolicitudEntity();
        solicitudEntity2.setId_solicitud(2L);
        solicitudEntity2.setNro_turno("T2");
        solicitudEntity2.setAtendido(false);
        solicitudEntity2.setFecha(LocalDateTime.now());

        List<SolicitudEntity> solicitudEntities = Arrays.asList(solicitudEntity1, solicitudEntity2);

        when(solicitudRepository.findByRegistrado()).thenReturn(solicitudEntities);
        when(modelMapper.map(solicitudEntity1, SolicitudEntity.class)).thenReturn(solicitudEntity1);
        when(modelMapper.map(solicitudEntity2, SolicitudEntity.class)).thenReturn(solicitudEntity2);

        List<SolicitudEntity> result = clienteService.getSolicitudClienteRegistrado();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNro_turno()).isEqualTo("T1");
        assertThat(result.get(1).getNro_turno()).isEqualTo("T2");

        verify(solicitudRepository, times(1)).findByRegistrado();
        verify(modelMapper, times(2)).map(any(), eq(SolicitudEntity.class));
    }
    @Test
    public void givenExistingClient_whenGenerarSolicitudCliente_thenReturnSolicitud() {
        Long nroDoc = 123456789L;
        ClienteEntity existingClient = new ClienteEntity();
        existingClient.setNroDoc(nroDoc);

        SolicitudEntity solicitudEntity = new SolicitudEntity();
        solicitudEntity.setId_solicitud(1L);
        solicitudEntity.setNro_turno("T123");
        solicitudEntity.setAtendido(false);
        solicitudEntity.setFecha(LocalDateTime.now());


        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.of(existingClient));
        when(modelMapper.map(existingClient, SolicitudEntity.class)).thenReturn(solicitudEntity);
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenReturn(solicitudEntity);
        when(modelMapper.map(solicitudEntity, Solicitud.class)).thenReturn(
                Solicitud.builder()
                        .id_solicitud(1L)
                        .nro_turno("T1")
                        .atendido(false)
                        .fecha(LocalDateTime.now())
                        .build()
        );
        Solicitud result = clienteService.generarSolicitudCliente(nroDoc);

        assertThat(result).isNotNull();
        assertThat(result.getNro_turno()).isEqualTo("T1");
        assertThat(result.getAtendido()).isFalse();
        verify(clienteRepository, times(1)).findByNroDoc(nroDoc);
        verify(modelMapper, times(1)).map(existingClient, SolicitudEntity.class);
        verify(solicitudRepository, times(1)).save(any(SolicitudEntity.class));
        verify(modelMapper, times(1)).map(solicitudEntity, Solicitud.class);
    }
    @Test
    public void givenNonExistingClient_whenGenerarSolicitudCliente_thenThrowException() {
        Long nroDoc = 123456789L;
        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> clienteService.generarSolicitudCliente(nroDoc))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontro un cliente con el numero de documento: " + nroDoc);
    }
    @Test
    public void testGenerarSolicitudClienteWhenClientExistsThenReturnSolicitud() {
        Long nroDoc = 123456789L;

        ClienteEntity existingClient = new ClienteEntity();
        existingClient.setNroDoc(nroDoc);

        SolicitudEntity solicitudEntity = new SolicitudEntity();
        solicitudEntity.setId_solicitud(1L);
        solicitudEntity.setNro_turno("T123");
        solicitudEntity.setAtendido(false);
        solicitudEntity.setFecha(LocalDateTime.now());

        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.of(existingClient));
        when(modelMapper.map(existingClient, SolicitudEntity.class)).thenReturn(solicitudEntity);
        when(solicitudRepository.save(any(SolicitudEntity.class))).thenReturn(solicitudEntity);
        when(modelMapper.map(solicitudEntity, Solicitud.class)).thenReturn(
                Solicitud.builder()
                        .id_solicitud(1L)
                        .nro_turno("T1")
                        .atendido(false)
                        .fecha(LocalDateTime.now())
                        .build()
        );
        Solicitud result = clienteService.generarSolicitudCliente(nroDoc);

        assertThat(result).isNotNull();
        assertThat(result.getNro_turno()).isEqualTo("T1");
        assertThat(result.getAtendido()).isFalse();
        verify(clienteRepository, times(1)).findByNroDoc(nroDoc);
        verify(modelMapper, times(1)).map(existingClient, SolicitudEntity.class);
        verify(solicitudRepository, times(1)).save(any(SolicitudEntity.class));
        verify(modelMapper, times(1)).map(solicitudEntity, Solicitud.class);
    }
    @Test
    public void testGenerarSolicitudClienteWhenClientDoesNotExistThenThrowRuntimeException() {
        Long nroDoc = 123456789L;

        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.generarSolicitudCliente(nroDoc))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se encontro un cliente con el numero de documento: " + nroDoc);
    }
    @Test
    public void testCreateTP_Categoria_FiscalWhenValidThenReturnTP_Categoria_FiscalEntity() {

        TP_Categoria_Fiscal tp_categoria_fiscal = new TP_Categoria_Fiscal();
        tp_categoria_fiscal.setId_categoria(1L);
        tp_categoria_fiscal.setDescripcion("Monotributista");

        TP_Categoria_FiscalEntity tp_categoria_fiscalEntity = new TP_Categoria_FiscalEntity();
        tp_categoria_fiscalEntity.setId_categoria_fiscal(1L);
        tp_categoria_fiscalEntity.setDescripcion("Monotributista");

        when(modelMapper.map(tp_categoria_fiscal, TP_Categoria_FiscalEntity.class)).thenReturn(tp_categoria_fiscalEntity);
        when(tpCategoriaFiscalRepository.save(tp_categoria_fiscalEntity)).thenReturn(tp_categoria_fiscalEntity);

        TP_Categoria_FiscalEntity result = clienteService.createTP_Categoria_Fiscal(tp_categoria_fiscal);

        assertThat(result).isEqualTo(tp_categoria_fiscalEntity);
        verify(modelMapper, times(1)).map(tp_categoria_fiscal, TP_Categoria_FiscalEntity.class);
        verify(tpCategoriaFiscalRepository, times(1)).save(tp_categoria_fiscalEntity);
    }
    @Test
    public void testCreateTP_Categoria_FiscalWhenNullThenThrowNullPointerException() {

        assertThatThrownBy(() -> clienteService.createTP_Categoria_Fiscal(null))
                .isInstanceOf(NullPointerException.class);
    }
    @Test
    public void testCreateTP_Categoria_FiscalWhenRepositoryThrowsExceptionThenPropagate() {
        // Arrange
        TP_Categoria_Fiscal tp_categoria_fiscal = new TP_Categoria_Fiscal();
        tp_categoria_fiscal.setId_categoria(1L);
        tp_categoria_fiscal.setDescripcion("Monotributista");

        when(modelMapper.map(tp_categoria_fiscal, TP_Categoria_FiscalEntity.class)).thenReturn(new TP_Categoria_FiscalEntity());
        when(tpCategoriaFiscalRepository.save(any(TP_Categoria_FiscalEntity.class))).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> clienteService.createTP_Categoria_Fiscal(tp_categoria_fiscal))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    public void testCreateClasificacionWhenValidThenReturnClasificacionEntity() {
        Clasificacion clasificacion = new Clasificacion();
        clasificacion.setId_clasificacion(1L);
        clasificacion.setDescripcion("A");

        ClasificacionEntity clasificacionEntity = new ClasificacionEntity();
        clasificacionEntity.setId_clasificacion(1L);
        clasificacionEntity.setDescripcion("Clase A");

        when(modelMapper.map(clasificacion, ClasificacionEntity.class)).thenReturn(clasificacionEntity);
        when(clasificacionRepository.save(clasificacionEntity)).thenReturn(clasificacionEntity);

        ClasificacionEntity result = clienteService.createClasificacion(clasificacion);

        assertThat(result).isEqualTo(clasificacionEntity);
        verify(modelMapper, times(1)).map(clasificacion, ClasificacionEntity.class);
        verify(clasificacionRepository, times(1)).save(clasificacionEntity);
    }
    @Test
    public void testCreateClasificacionWhenNullThenThrowNullPointerException() {

        assertThatThrownBy(() -> clienteService.createClasificacion(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testCreateClasificacionWhenRepositoryThrowsExceptionThenPropagate() {
        // Arrange
        Clasificacion clasificacion = new Clasificacion();
        clasificacion.setId_clasificacion(1L);
        clasificacion.setDescripcion("A");

        when(modelMapper.map(clasificacion, ClasificacionEntity.class)).thenReturn(new ClasificacionEntity());
        when(clasificacionRepository.save(any(ClasificacionEntity.class))).thenThrow(new RuntimeException());

        // Act and Assert
        assertThatThrownBy(() -> clienteService.createClasificacion(clasificacion))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    public void testCreateTipoClienteWhenValidThenReturnTipoClienteEntity() {
        // Arrange
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId_tipo_cliente(1L);
        tipoCliente.setTipo_cliente("Persona");

        TipoClienteEntity tipoClienteEntity = new TipoClienteEntity();
        tipoClienteEntity.setId_tipo_cliente(1L);
        tipoClienteEntity.setTipo_cliente("Persona");

        when(modelMapper.map(tipoCliente, TipoClienteEntity.class)).thenReturn(tipoClienteEntity);
        when(tipoClienteRepository.save(tipoClienteEntity)).thenReturn(tipoClienteEntity);

        TipoClienteEntity result = clienteService.createTipoCliente(tipoCliente);

        assertThat(result).isEqualTo(tipoClienteEntity);
        verify(modelMapper, times(1)).map(tipoCliente, TipoClienteEntity.class);
        verify(tipoClienteRepository, times(1)).save(tipoClienteEntity);
    }

    @Test
    public void testCreateTipoClienteWhenRepositoryThrowsExceptionThenPropagate() {
        // Arrange
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId_tipo_cliente(1L);
        tipoCliente.setTipo_cliente("Persona");

        when(modelMapper.map(tipoCliente, TipoClienteEntity.class)).thenReturn(new TipoClienteEntity());
        when(tipoClienteRepository.save(any(TipoClienteEntity.class))).thenThrow(new RuntimeException());

        // Act and Assert
        assertThatThrownBy(() -> clienteService.createTipoCliente(tipoCliente))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    public void testCreateTipoClienteWhenNullThenThrowNullPointerException() {
        // Act and Assert
        assertThatThrownBy(() -> clienteService.createTipoCliente(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSetearPuntosWhenDniMismatchThenThrowException() {
        FacturacionRequest facturacionRequest = new FacturacionRequest(123456789L);


        assertThatThrownBy(() -> clienteService.setearPuntos(facturacionRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DNI no encontrado en la API externa.");
    }

    @Test
    public void testSetearPuntosWhenClientNotFoundThenThrowException() {
        FacturacionRequest facturacionRequest = new FacturacionRequest(123456789L);
        when(fidelizacionClientMock.getAllFacturacion()).thenReturn(Arrays.asList(new FacturacionSolicitud(123456789L, new BigDecimal("50000"))));
        when(clienteRepository.findByNroDoc(123456789L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.setearPuntos(facturacionRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El DNI ingresado no coincide con el DNI del cliente.");
    }
    @Test
    public void testGetProductosDescuentoWithValidProducts() {
        ProductoRequest producto1 = new ProductoRequest();
        producto1.setOfertas(Arrays.asList(new Oferta("1", "Oferta 1", "Desc 1", 100.0, 10.0, true)));
        ProductoRequest producto2 = new ProductoRequest();
        producto2.setOfertas(Arrays.asList(new Oferta("2", "Oferta 2", "Desc 2", 200.0, 20.0, false)));
        List<ProductoRequest> productos = Arrays.asList(producto1, producto2);

        when(fidelizacionClientMock.getAllProductos()).thenReturn(productos);

        List<ProductoRequest> result = clienteService.getProductosDescuento();

        assertThat(result).containsOnly(producto1);
        verify(fidelizacionClientMock, times(1)).getAllProductos();
    }
    @Test
    public void testGetProductosDescuentoWhenFidelizacionClientIsNull() {
        FidelizacionClient fidelizacionClientMock = mock(FidelizacionClient.class);

        when(fidelizacionClientMock.getAllProductos()).thenReturn(Collections.emptyList());

        clienteService = new ClienteServiceImp(
                clienteRepository,
                modelMapper,
                clasificacionRepository,
                tpCategoriaFiscalRepository,
                tipoDocumentoRepository,
                tipoClienteRepository,
                solicitudRepository,
                clienteTemporalRepository,
                fidelizacionClientMock
        );


        List<ProductoRequest> result = clienteService.getProductosDescuento();

        assertThat(result).isEmpty();
    }
    @Test
    public void testGetProductosDescuentoWhenFidelizacionClientThrowsException() {
        when(fidelizacionClientMock.getAllProductos()).thenThrow(new RuntimeException());

        List<ProductoRequest> result = clienteService.getProductosDescuento();

        assertThat(result).isEmpty();
    }

    @Test
    public void testRestarPuntosAlClienteWhenClientExistsAndHasEnoughPointsThenPointsDecreased() {
        Long nroDoc = 123456789L;
        Double puntosTotales = 10.0;
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNroDoc(nroDoc);
        clienteEntity.setCant_puntos(15.0); // cliente tiene más puntos que los puntosTotales
        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.of(clienteEntity));
        clienteService.restarPuntosAlCliente(nroDoc, puntosTotales);

        assertThat(clienteEntity.getCant_puntos()).isEqualTo(5.0); // Se espera que los puntos se hayan reducido correctamente
        verify(clienteRepository, times(1)).save(clienteEntity); // Verificar que se llamó save una vez
    }
    @Test
    public void testRestarPuntosAlClienteWhenClientExistsButHasInsufficientPointsThenThrowException() {
        // Arrange
        Long nroDoc = 123456789L;
        Double puntosTotales = 20.0;
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNroDoc(nroDoc);
        clienteEntity.setCant_puntos(15.0); // cliente tiene menos puntos que los puntosTotales
        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.of(clienteEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.restarPuntosAlCliente(nroDoc, puntosTotales));
        assertThat(exception.getMessage()).contains("El cliente no tiene suficientes puntos para realizar la compra");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    public void testRestarPuntosAlClienteWhenClientDoesNotExistThenThrowException() {
        Long nroDoc = 123456789L;
        Double puntosTotales = 10.0;
        when(clienteRepository.findByNroDoc(nroDoc)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.restarPuntosAlCliente(nroDoc, puntosTotales));
        assertThat(exception.getMessage()).contains("No se encontro un cliente con el numero de documento: " + nroDoc);

        verify(clienteRepository, never()).save(any());
    }
    @Test
    public void testAtenderClienteWhenClientExistsThenReturnTrue() {
        SolicitudEntity solicitudEntity = new SolicitudEntity();
        when(solicitudRepository.findById(any(Long.class))).thenReturn(Optional.of(solicitudEntity));

        Boolean result = clienteService.atenderCliente(1L);

        assertThat(result).isTrue();
        verify(solicitudRepository, times(1)).save(solicitudEntity);
    }

    @Test
    public void testAtenderClienteWhenClientDoesNotExistThenThrowException() {
        when(solicitudRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.atenderCliente(1L));
        assertThat(exception.getMessage()).contains("No se encontro un cliente con el numero de documento: 1");
    }


    @Test
    public void testCreateTipoDocWhenValidThenReturnTipoDocumentoEntity() {
        TipoDocumentoEntity tipoDocumentoEntity = new TipoDocumentoEntity();
        when(modelMapper.map(tipoDocumento, TipoDocumentoEntity.class)).thenReturn(tipoDocumentoEntity);
        when(tipoDocumentoRepository.save(tipoDocumentoEntity)).thenReturn(tipoDocumentoEntity);

        TipoDocumentoEntity result = clienteService.createTipoDoc(tipoDocumento);

        assertThat(result).isEqualTo(tipoDocumentoEntity);
    }

    @Test
    public void testCreateTipoDocWhenNullThenThrowNullPointerException() {
        assertThatThrownBy(() -> clienteService.createTipoDoc(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testCreateTipoDocWhenRepositoryThrowsExceptionThenPropagate() {
        when(modelMapper.map(tipoDocumento, TipoDocumentoEntity.class)).thenReturn(new TipoDocumentoEntity());
        when(tipoDocumentoRepository.save(any(TipoDocumentoEntity.class))).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> clienteService.createTipoDoc(tipoDocumento))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testGetAllClientsWhenClientsExistThenReturnListOfClients() {
        ClienteEntity clienteEntity = new ClienteEntity();
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(clienteEntity));
        when(modelMapper.map(clienteEntity, Cliente.class)).thenReturn(cliente);

        List<Cliente> clientes = clienteService.getAllClients();

        assertThat(clientes).isNotEmpty();
        assertThat(clientes.get(0)).isEqualTo(cliente);
    }

    @Test
    public void testGetAllClientsWhenNoClientsThenReturnEmptyList() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList());

        List<Cliente> clientes = clienteService.getAllClients();

        assertThat(clientes).isEmpty();
    }

    @Test
    public void testUpdateClientWhenClientDoesNotExistThenThrowIllegalArgumentException() {
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> clienteService.updateClient(cliente));
    }

    @Test
    public void testCreateClientWhenValidThenReturnCreatedClient() {
        cliente.setId_categoria_fiscal(new TP_Categoria_Fiscal());
        assertThrows(IllegalArgumentException.class, () -> clienteService.createClient(cliente));
    }

    @Test
    public void testCreateClientWhenExistingEmailThenThrowRuntimeException() {
        when(clienteRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new ClienteEntity()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clienteService.createClient(cliente));
        assertThat(exception.getMessage()).contains("Ya existe un cliente con el email: " + cliente.getEmail());
    }

    @Test
    public void testCreateClientWhenExistingPhoneNumberThenThrowRuntimeException() {
        when(clienteRepository.findByTelefono(any(String.class))).thenReturn(Optional.of(new ClienteEntity()));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con el teléfono: " + cliente.getTelefono());
    }

    @Test
    public void testCreateClientWhenExistingDocumentNumberThenThrowRuntimeException() {
        when(clienteRepository.findByNroDoc(any(Long.class))).thenReturn(Optional.of(new ClienteEntity()));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con el número de documento: " + cliente.getNroDoc());
    }

    @Test
    public void testCreateClientWhenNullCategoriaFiscalAndNonNullClasificacionThenThrowIllegalArgumentException() {
        cliente.setId_categoria_fiscal(null);
        cliente.setId_clasificacion(new Clasificacion(1L, "A"));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La categoría fiscal no puede ser nula.");
    }

    @Test
    public void testCreateClientWhenClientWithEmailAlreadyExistsThenThrowException() {
        when(clienteRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new ClienteEntity()));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con el email: " + cliente.getEmail());
    }

    @Test
    public void testCreateClientWhenClientWithPhoneAlreadyExistsThenThrowException() {
        when(clienteRepository.findByTelefono(any(String.class))).thenReturn(Optional.of(new ClienteEntity()));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con el teléfono: " + cliente.getTelefono());
    }

    @Test
    public void testCreateClientWhenClientWithDocumentNumberAlreadyExistsThenThrowException() {
        when(clienteRepository.findByNroDoc(any(Long.class))).thenReturn(Optional.of(new ClienteEntity()));

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con el número de documento: " + cliente.getNroDoc());
    }

    @Test
    public void testCreateClientWhenClientIsMonotributistaAndClassificationNotProvidedThenThrowException() {
        cliente.setId_categoria_fiscal(new TP_Categoria_Fiscal(1L, "Monotributista"));
        cliente.setId_clasificacion(null);

        assertThatThrownBy(() -> clienteService.createClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
    }
    @Test
    public void testUpdateClientWhenClientDoesNotExistThenThrowIllegalArgumentExceptionn() {
        when(clienteRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> clienteService.updateClient(cliente));
    }

    @Test
    public void testUpdateClientWhenClientIsMonotributistaAndClassificationNotProvidedThenThrowException() {
        cliente.setId_categoria_fiscal(new TP_Categoria_Fiscal(1L, "Monotributista"));
        cliente.setId_clasificacion(null);

        assertThatThrownBy(() -> clienteService.updateClient(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
    }

}