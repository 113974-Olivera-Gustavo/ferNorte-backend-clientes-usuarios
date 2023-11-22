    package com.example.micro_a.services.imp;

    import com.example.micro_a.dtos.FacturacionRequest;
    import com.example.micro_a.dtos.catalogo.CompraRequest;
    import com.example.micro_a.dtos.catalogo.ProductoRequest;
    import com.example.micro_a.dtos.catalogo.ProductosCanjeados;
    import com.example.micro_a.entities.*;
    import com.example.micro_a.models.*;
    import com.example.micro_a.repositories.*;
    import com.example.micro_a.services.ClienteService;
    import com.example.micro_a.templates.FidelizacionClient;
    import org.modelmapper.ModelMapper;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.time.LocalDateTime;
    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class ClienteServiceImp implements ClienteService {


        private final ClienteRepository clienteRepository;
        private final ModelMapper modelMapper;
        private final ClasificacionRepository clasificacionRepository;
        private final TpCategoriaFiscalRepository tpCategoriaFiscalRepository;
        private final TipoDocumentoRepository tipoDocumentoRepository;
        private final TipoClienteRepository tipoClienteRepository;
        private final SolicitudRepository solicitudRepository;
        private final ClienteTemporalRepository clienteTemporalRepository;
        private final FidelizacionClient fidelizacionClient;

        @Autowired
        public ClienteServiceImp(ClienteRepository clienteRepository, ModelMapper modelMapper, ClasificacionRepository clasificacionRepository, TpCategoriaFiscalRepository tpCategoriaFiscalRepository, TipoDocumentoRepository tipoDocumentoRepository, TipoClienteRepository tipoClienteRepository, SolicitudRepository solicitudRepository, ClienteTemporalRepository clienteTemporalRepository, FidelizacionClient fidelizacionClient) {
            this.clienteRepository = clienteRepository;
            this.modelMapper = modelMapper;
            this.clasificacionRepository = clasificacionRepository;
            this.tpCategoriaFiscalRepository = tpCategoriaFiscalRepository;
            this.tipoDocumentoRepository = tipoDocumentoRepository;
            this.tipoClienteRepository = tipoClienteRepository;
            this.solicitudRepository = solicitudRepository;
            this.clienteTemporalRepository = clienteTemporalRepository;
            this.fidelizacionClient = fidelizacionClient;
        }

        @Override
        public Cliente createClient(Cliente cliente) {
            if (cliente.getId_categoria_fiscal() != null) {
                if (cliente.getId_categoria_fiscal().getId_categoria() == null) {
                    throw new IllegalArgumentException("El ID de la categoría fiscal no puede ser nulo.");
                }

                if (cliente.getId_categoria_fiscal().getId_categoria() == 2 || cliente.getId_categoria_fiscal().getId_categoria() == 3) {
                    // Si es "Responsable Inscripto", clasificación es nula
                    cliente.setId_clasificacion(null);
                } else if (cliente.getId_categoria_fiscal().getId_categoria() == 1 && cliente.getId_clasificacion() == null) {
                    throw new IllegalArgumentException("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
                } else if (cliente.getId_categoria_fiscal().getId_categoria() == null && cliente.getId_clasificacion() != null) {
                    throw new IllegalArgumentException("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
                }
            } else {
                throw new IllegalArgumentException("La categoría fiscal no puede ser nula.");
            }

            Optional<ClienteEntity> existingNroDoc = clienteRepository.findByNroDoc(cliente.getNroDoc());
            if (existingNroDoc.isPresent()) {
                throw new RuntimeException("Ya existe un cliente con el número de documento: " + cliente.getNroDoc());
            }

            Optional<ClienteEntity> existingEmail = clienteRepository.findByEmail(cliente.getEmail());
            if (existingEmail.isPresent()) {
                throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getEmail());
            }

            Optional<ClienteEntity> existingTelefono = clienteRepository.findByTelefono(cliente.getTelefono());
            if (existingTelefono.isPresent()) {
                throw new RuntimeException("Ya existe un cliente con el teléfono: " + cliente.getTelefono());
            }

            ClienteEntity clienteEntity = modelMapper.map(cliente, ClienteEntity.class);
            ClienteEntity clienteEntitySaved = clienteRepository.save(clienteEntity);
            return modelMapper.map(clienteEntitySaved, Cliente.class);
        }




        @Override
        public List<Cliente> getAllClients() {
            List<ClienteEntity> clienteEntities = clienteRepository.findAll();
            return clienteEntities.stream().map(clienteEntity -> modelMapper.map(clienteEntity, Cliente.class)).collect(Collectors.toList());
        }

        @Override
        public List<TipoCliente> getTipoCliente() {
            List<TipoClienteEntity> tipoCliente = tipoClienteRepository.findAll();
            return tipoCliente.stream().map(tipoClienteEntity -> modelMapper.map(tipoClienteEntity, TipoCliente.class)).collect(Collectors.toList());
        }

        @Override
        public List<Clasificacion> getClasificacion() {
            List<ClasificacionEntity> clasificacion = clasificacionRepository.findAll();
            return clasificacion.stream().map(clasificacionEntity -> modelMapper.map(clasificacionEntity, Clasificacion.class)).collect(Collectors.toList());
        }

        @Override
        public List<TP_Categoria_Fiscal> getTPCategoriaFiscal() {
            List<TP_Categoria_FiscalEntity> tpCategoriaFiscal = tpCategoriaFiscalRepository.findAll();
            return tpCategoriaFiscal.stream().map(tp_categoria_fiscalEntity -> modelMapper.map(tp_categoria_fiscalEntity, TP_Categoria_Fiscal.class)).collect(Collectors.toList());
        }

        @Override
        public List<TipoDocumento> getTipoDocumento() {
            List<TipoDocumentoEntity> tipoDocumento = tipoDocumentoRepository.findAll();
            return tipoDocumento.stream().map(tipoDocumentoEntity -> modelMapper.map(tipoDocumentoEntity, TipoDocumento.class)).collect(Collectors.toList());
        }

        @Override
        public List<Cliente> getClienteByDni(Long nroDoc) {
            Optional<ClienteEntity> existingClient = clienteRepository.findByNroDoc(nroDoc);
            if (existingClient.isPresent()) {
                ClienteEntity clienteEntity = existingClient.get();
                return List.of(modelMapper.map(clienteEntity, Cliente.class));
            } else {
                throw new RuntimeException("No se encontro un cliente con el numero de documento: " + nroDoc);
            }
        }

        @Override
        public Cliente updateClient(Cliente cliente) {
            if (cliente.getId() == null) {
                throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
            }
            if (cliente.getId_categoria_fiscal() == null) {
                throw new IllegalArgumentException("La categoría fiscal no puede ser nula.");
            }

            if (cliente.getId_categoria_fiscal().getId_categoria() != null) {
                if (cliente.getId_categoria_fiscal().getId_categoria() == 1 && cliente.getId_clasificacion() == null) {
                    throw new IllegalArgumentException("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
                } else if (cliente.getId_categoria_fiscal().getId_categoria() == null && cliente.getId_clasificacion() != null) {
                    throw new IllegalArgumentException("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
                }
            } else {
                throw new IllegalArgumentException("El ID de la categoría fiscal no puede ser nulo.");
            }
            
            Optional<ClienteEntity> clienteEntityOptional = clienteRepository.findById(cliente.getId());

            if (clienteEntityOptional.isPresent()) {
                ClienteEntity clienteEntity = clienteEntityOptional.get();

                // Verificar que el DNI no esté registrado para otro cliente
                if (!clienteEntity.getNroDoc().equals(cliente.getNroDoc()) && clienteRepository.findByNroDoc(cliente.getNroDoc()).isPresent()) {
                    throw new IllegalArgumentException("El DNI ya está registrado para otro cliente.");
                }

                // Verificar que el email no esté registrado para otro cliente
                if (!clienteEntity.getEmail().equals(cliente.getEmail()) && clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("El email ya está registrado para otro cliente.");
                }

                // Verificar que el teléfono no esté registrado para otro cliente
                if (!clienteEntity.getTelefono().equals(cliente.getTelefono()) && clienteRepository.findByTelefono(cliente.getTelefono()).isPresent()) {
                    throw new IllegalArgumentException("El teléfono ya está registrado para otro cliente.");
                }

                // Actualizar los campos permitidos
                clienteEntity.setNombre(cliente.getNombre());
                clienteEntity.setApellido(cliente.getApellido());
                clienteEntity.setEmail(cliente.getEmail());
                clienteEntity.setTelefono(cliente.getTelefono());
                clienteEntity.setId_tipo_doc(modelMapper.map(cliente.getId_tipo_doc(), TipoDocumentoEntity.class));
                clienteEntity.setDomicilio(cliente.getDomicilio());

                // Actualizar categoría fiscal
                if (cliente.getId_categoria_fiscal() != null) {
                    TP_Categoria_FiscalEntity categoriaFiscalEntity = modelMapper.map(cliente.getId_categoria_fiscal(), TP_Categoria_FiscalEntity.class);

                    if (cliente.getId_categoria_fiscal().getId_categoria() != null) {
                        categoriaFiscalEntity = tpCategoriaFiscalRepository.getOne(cliente.getId_categoria_fiscal().getId_categoria());
                    } else {
                        categoriaFiscalEntity = tpCategoriaFiscalRepository.save(categoriaFiscalEntity);
                    }

                    clienteEntity.setId_categoria_fiscal(categoriaFiscalEntity);
                } else {
                    clienteEntity.setId_categoria_fiscal(null);
                }

                // Actualizar tipo de cliente
                if (cliente.getId_tipo_cliente() != null) {
                    clienteEntity.setId_tipo_cliente(modelMapper.map(cliente.getId_tipo_cliente(), TipoClienteEntity.class));
                }

                // Actualizar clasificación si es Monotributista
                if (cliente.getId_categoria_fiscal() != null && cliente.getId_categoria_fiscal().getId_categoria() == 1) {
                    if (cliente.getId_clasificacion() == null) {
                        throw new IllegalArgumentException("La clasificación es obligatoria para la categoría fiscal 'Monotributista'.");
                    } else {
                        clienteEntity.setId_clasificacion(modelMapper.map(cliente.getId_clasificacion(), ClasificacionEntity.class));
                    }
                } else if (cliente.getId_categoria_fiscal() != null && cliente.getId_categoria_fiscal().getId_categoria() == 2) {
                    // Si es Responsable Inscripto, no debe tener clasificación
                    clienteEntity.setId_clasificacion(null);
                }

                clienteEntity.setCant_puntos(cliente.getCant_puntos());

                // Guardar el cliente actualizado
                ClienteEntity updatedClienteEntity = clienteRepository.save(clienteEntity);

                // Mapear el ClienteEntity a Cliente y devolverlo
                return modelMapper.map(updatedClienteEntity, Cliente.class);
            } else {
                throw new IllegalArgumentException("No se encontró el cliente con ID: " + cliente.getId());
            }
        }



        @Override
        public TipoDocumentoEntity createTipoDoc(TipoDocumento tipoDocumento) {
            if (tipoDocumento == null) {
                throw new NullPointerException("tipoDocumento no puede ser null");
            }

            TipoDocumentoEntity tipoDocumentoEntity = modelMapper.map(tipoDocumento, TipoDocumentoEntity.class);
            return tipoDocumentoRepository.save(tipoDocumentoEntity);
        }

        @Override
        public TipoClienteEntity createTipoCliente(TipoCliente tipoClienteEntity) {
            if(tipoClienteEntity == null){
                throw new NullPointerException("tipoCliente no puede ser null");
            }
            TipoClienteEntity tipoClienteEntity1 = modelMapper.map(tipoClienteEntity, TipoClienteEntity.class);
            return tipoClienteRepository.save(tipoClienteEntity1);
        }

        @Override
        public ClasificacionEntity createClasificacion(Clasificacion clasificacion) {
            if(clasificacion == null){
                throw new NullPointerException("clasificacion no puede ser null");
            }
            ClasificacionEntity clasificacionEntity = modelMapper.map(clasificacion, ClasificacionEntity.class);
            return clasificacionRepository.save(clasificacionEntity);
        }

        @Override
        public TP_Categoria_FiscalEntity createTP_Categoria_Fiscal(TP_Categoria_Fiscal tp_categoria_fiscal) {
            if(tp_categoria_fiscal == null){
                throw new NullPointerException("tp_categoria_fiscal no puede ser null");
            }
            TP_Categoria_FiscalEntity tp_categoria_fiscalEntity = modelMapper.map(tp_categoria_fiscal, TP_Categoria_FiscalEntity.class);
            return tpCategoriaFiscalRepository.save(tp_categoria_fiscalEntity);
        }


        @Override
        public Solicitud generarSolicitudCliente(Long nroDoc){
            Optional<ClienteEntity> existingClient = clienteRepository.findByNroDoc(nroDoc);
            if(existingClient.isPresent()){
                SolicitudEntity solicitudEntity = modelMapper.map(existingClient.get(), SolicitudEntity.class);

                solicitudEntity.setAtendido(false);
                solicitudEntity.setFecha(LocalDateTime.now());
                solicitudEntity.setNro_turno(generarNumeroTurno());

                SolicitudEntity solicitudEntitySaved = solicitudRepository.save(solicitudEntity);
                return modelMapper.map(solicitudEntitySaved, Solicitud.class);
            }
            else{
                throw new RuntimeException("No se encontro un cliente con el numero de documento: " + nroDoc);
            }

        }

        @Override
        public Solicitud generarSolicitudClienteTemporal(ClienteTemporal clienteTemporal) {
            ClienteTemporalEntity clienteTemporalEntity = modelMapper.map(clienteTemporal, ClienteTemporalEntity.class);
            clienteTemporalEntity = clienteTemporalRepository.save(clienteTemporalEntity);
            SolicitudEntity solicitudEntity = SolicitudEntity.builder()
                    .atendido(false)
                    .fecha(LocalDateTime.now())
                    .nro_turno(generarNumeroTurno())
                    .clienteTemporal(clienteTemporalEntity)
                    .build();
            SolicitudEntity solicitudSaved = solicitudRepository.save(solicitudEntity);

            return modelMapper.map(solicitudSaved, Solicitud.class);
        }

        @Override
        public List<SolicitudEntity> getSolicitudClienteRegistrado() {
            List<SolicitudEntity> list = solicitudRepository.findByRegistrado();
            return list.stream().map(solicitudEntity -> modelMapper.map(solicitudEntity, SolicitudEntity.class)).collect(Collectors.toList());
        }
        @Override
        public List<SolicitudEntity> getSolicitudClienteAtendido() {
            List<SolicitudEntity> list = solicitudRepository.findByAtendido();
            return list.stream().map(solicitudEntity -> modelMapper.map(solicitudEntity, SolicitudEntity.class)).collect(Collectors.toList());
        }

        @Override
        public Cliente setearPuntos(FacturacionRequest facturacionRequest) {
            Long nroDocAPI = facturacionRequest.getNroDoc();

            List<FacturacionSolicitud> facturaciones = fidelizacionClient.getAllFacturacion();

            Optional<FacturacionSolicitud> facturacionOptional = facturaciones.stream()
                    .filter(f -> f.getNroDoc().equals(nroDocAPI))
                    .findFirst();

            if (facturacionOptional.isPresent()) {
                FacturacionSolicitud facturacionSolicitud = facturacionOptional.get();

                Optional<ClienteEntity> clienteOptional = clienteRepository.findByNroDoc(nroDocAPI);

                if (clienteOptional.isPresent()) {
                    ClienteEntity cliente = clienteOptional.get();

                    if (cliente.getNroDoc().equals(nroDocAPI)) {
                        // Obtenemos el monto total facturado por el cliente desde la API
                        BigDecimal totalAmountBilledAPI = facturacionSolicitud.getTotalAmountBilled();
                        Double puntosAdicionales = totalAmountBilledAPI.divide(new BigDecimal("5000"), RoundingMode.FLOOR).doubleValue() * 10;
                        // Sumamos los nuevos puntos a los puntos existentes
                        Double nuevosPuntos = cliente.getCant_puntos() + puntosAdicionales;

                        // Actualizamos los puntos del cliente en la entidad y guardamos en la base de datos
                        cliente.setCant_puntos(nuevosPuntos);
                        clienteRepository.save(cliente);

                        return modelMapper.map(cliente, Cliente.class);
                    } else {
                        throw new RuntimeException("El DNI ingresado no coincide con el DNI del cliente.");
                    }
                } else {
                    throw new RuntimeException("El DNI ingresado no coincide con el DNI del cliente.");
                }
            } else {
                throw new RuntimeException("DNI no encontrado en la API externa.");
            }
        }


        @Override
        public List<ProductoRequest> getProductosDescuento() {
            try {
                if (fidelizacionClient == null) {
                    throw new RuntimeException("El cliente de fidelización no está disponible");
                }
                List<ProductoRequest> productos = fidelizacionClient.getAllProductos();
                return productos.stream()
                        .filter(this::tieneOfertasActiva)
                        .collect(Collectors.toList());
            } catch (RuntimeException e) {
                return Collections.emptyList();
            }
        }

        @Override
        public void procesarCompra(CompraRequest compra) {
            List<ProductoRequest> productosOferta = getProductosDescuento();
            for (ProductosCanjeados productosCanjeados : compra.getProductosCanjeados()){
                String codigoProducto = productosCanjeados.getCodigo();
                int cantidadComprada = productosCanjeados.getCantidad();

                //Encontrar codigo en la oferta
                Optional<ProductoRequest> productoOptional = productosOferta.stream()
                        .filter(producto -> producto.getCodigo().equals(codigoProducto))
                        .findFirst();
                if(productoOptional.isPresent()){
                    Double puntosPorProducto = productoOptional.get().getOfertas().get(0).getPuntos();
                    Double puntosTotales = puntosPorProducto * cantidadComprada;

                    restarPuntosAlCliente(compra.getNroDoc(), puntosTotales);
                }else {
                    throw new RuntimeException("Producto no encontrado en la oferta: "+codigoProducto);
                }
            }
        }

        public void restarPuntosAlCliente(Long nroDoc, Double puntosTotales) {
            Optional<ClienteEntity> cliente = clienteRepository.findByNroDoc(nroDoc);
            if(cliente.isPresent()){
                if(cliente.get().getCant_puntos() < puntosTotales){
                    throw new RuntimeException("El cliente no tiene suficientes puntos para realizar la compra");
                }
                ClienteEntity clienteEntity = cliente.get();
                Double puntosActuales = clienteEntity.getCant_puntos();
                Double nuevosPuntos = Math.max(0, puntosActuales - puntosTotales);

                clienteEntity.setCant_puntos(nuevosPuntos);
                clienteRepository.save(clienteEntity);
            }
            else {
                throw new RuntimeException("No se encontro un cliente con el numero de documento: " + nroDoc);
            }
        }

        @Override
        public Boolean atenderCliente(Long id) {
            Optional <SolicitudEntity> clientesAtendidos = solicitudRepository.findById(id);
            if(clientesAtendidos.isPresent()){
                SolicitudEntity solicitud = clientesAtendidos.get();
                solicitud.setAtendido(true);
                solicitudRepository.save(solicitud);
                return true;
            }
            else {
                throw new RuntimeException("No se encontro un cliente con el numero de documento: " + id);
            }
        }

        private String generarNumeroTurno(){
            return "T" + (solicitudRepository.count()+1);
        }
        private Boolean tieneOfertasActiva(ProductoRequest producto){
            return producto.getOfertas().stream().anyMatch(oferta -> oferta.getActivo());
        }


    }
