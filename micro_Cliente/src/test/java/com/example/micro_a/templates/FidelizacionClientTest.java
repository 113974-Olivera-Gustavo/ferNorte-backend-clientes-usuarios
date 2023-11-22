package com.example.micro_a.templates;

import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.models.FacturacionSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FidelizacionClientTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private FidelizacionClient fidelizacionClient;

    private ProductoRequest[] productos;
    private FacturacionSolicitud[] facturaciones;
    private String facturacion_URL = "http://my-json-server.typicode.com/114084-DIBELLA-THIAGO/billedClients/listado";
    private String catalogoUrl = "https://my-json-server.typicode.com/113974-Olivera-Gustavo/api-catalogo-bd/productos";

    @BeforeEach
    public void setUp() {
        facturaciones = new FacturacionSolicitud[]{
                new FacturacionSolicitud(1L, new BigDecimal("100.00")),
                new FacturacionSolicitud(2L, new BigDecimal("200.00"))
        };
        productos = new ProductoRequest[]{
                new ProductoRequest(),
                new ProductoRequest()
        };
    }

    @Test
    public void testGetAllFacturacionWhenRestApiReturnsValidResponseThenReturnListOfFacturacionSolicitud() {
        when(restTemplate.exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class))
                .thenReturn(ResponseEntity.ok(facturaciones));

        List<FacturacionSolicitud> result = fidelizacionClient.getAllFacturacion();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class);
    }

    @Test
    public void testGetAllFacturacionWhenRestApiReturnsEmptyResponseThenReturnEmptyList() {
        when(restTemplate.exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class))
                .thenReturn(ResponseEntity.ok(new FacturacionSolicitud[0]));

        List<FacturacionSolicitud> result = fidelizacionClient.getAllFacturacion();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class);
    }

    @Test
    public void testGetAllFacturacionWhenRestApiReturnsErrorThenThrowException() {
        when(restTemplate.exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> fidelizacionClient.getAllFacturacion());
        verify(restTemplate, times(1)).exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class);
    }

    @Test
    public void testGetAllProductosWhenRestApiReturnsErrorThenThrowException() {
        when(restTemplate.exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> fidelizacionClient.getAllProductos());
        verify(restTemplate, times(1)).exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class);
    }
    @Test
    public void testGetAllProductosWhenRestApiReturnsEmptyResponseThenReturnEmptyList() {
        when(restTemplate.exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class))
                .thenReturn(ResponseEntity.ok(new ProductoRequest[0]));

        List<ProductoRequest> result = fidelizacionClient.getAllProductos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class);
    }
    @Test
    public void testGetAllProductosWhenRestApiReturnsValidResponseThenReturnListOfProductoRequest() {
        when(restTemplate.exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class))
                .thenReturn(ResponseEntity.ok(productos));

        List<ProductoRequest> result = fidelizacionClient.getAllProductos();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).exchange(catalogoUrl, HttpMethod.GET, null, ProductoRequest[].class);
    }
}