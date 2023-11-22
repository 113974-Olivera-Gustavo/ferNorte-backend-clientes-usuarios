package com.example.micro_a.services;

import com.example.micro_a.dtos.FacturacionRequest;
import com.example.micro_a.dtos.catalogo.CompraRequest;
import com.example.micro_a.dtos.catalogo.ProductoRequest;
import com.example.micro_a.entities.*;
import com.example.micro_a.models.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClienteService {
    Cliente createClient(Cliente cliente);
    List<Cliente> getAllClients();
    List<TipoCliente> getTipoCliente();
    List<Clasificacion>getClasificacion();
    List<TP_Categoria_Fiscal> getTPCategoriaFiscal();
    List<TipoDocumento>getTipoDocumento();
    List<Cliente> getClienteByDni(Long nroDoc);
    Cliente updateClient(Cliente cliente);
    TipoDocumentoEntity createTipoDoc(TipoDocumento tipoDocumentoRequest);
    TipoClienteEntity createTipoCliente(TipoCliente tipoClienteEntity);
    ClasificacionEntity createClasificacion(Clasificacion clasificacion);
    TP_Categoria_FiscalEntity createTP_Categoria_Fiscal(TP_Categoria_Fiscal tp_categoria_fiscal);
    Solicitud generarSolicitudCliente(Long nro_doc);
    Solicitud generarSolicitudClienteTemporal(ClienteTemporal clienteTemporal);
    List<SolicitudEntity> getSolicitudClienteRegistrado();
    Boolean atenderCliente(Long id);
    List<SolicitudEntity> getSolicitudClienteAtendido();
    Cliente setearPuntos(FacturacionRequest facturacionRequest);
    List<ProductoRequest> getProductosDescuento();
    void procesarCompra(CompraRequest compra);
}
