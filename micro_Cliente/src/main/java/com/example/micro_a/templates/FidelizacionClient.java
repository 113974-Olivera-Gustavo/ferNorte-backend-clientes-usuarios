package com.example.micro_a.templates;

import com.example.micro_a.dtos.catalogo.CompraRequest;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.models.FacturacionSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FidelizacionClient {
    private final RestTemplate restTemplate;

    private String facturacion_URL = "http://my-json-server.typicode.com/114084-DIBELLA-THIAGO/billedClients/listado";
    private String catalogoUrl = "https://my-json-server.typicode.com/113974-Olivera-Gustavo/api-catalogo-bd/productos";
    private String compraUrl = "https://my-json-server.typicode.com/113974-Olivera-Gustavo/api-compra-db/compras";

    public List<FacturacionSolicitud> getAllFacturacion() {
        ResponseEntity<FacturacionSolicitud[]> response = restTemplate.exchange(facturacion_URL, HttpMethod.GET, null, FacturacionSolicitud[].class);
        FacturacionSolicitud[] facturaciones = response.getBody();
        return Arrays.asList(facturaciones);
    }
    public List<ProductoRequest> getAllProductos(){
        ResponseEntity<ProductoRequest[]> response = restTemplate.exchange(catalogoUrl, HttpMethod.GET,null, ProductoRequest[].class);
        ProductoRequest[] productos = response.getBody();
        return Arrays.asList(productos);
    }
    public List<CompraRequest> getAllCompras(){
        ResponseEntity<CompraRequest[]> response = restTemplate.exchange(compraUrl, HttpMethod.GET,null, CompraRequest[].class);
        CompraRequest[] compras = response.getBody();
        return Arrays.asList(compras);
    }
}
