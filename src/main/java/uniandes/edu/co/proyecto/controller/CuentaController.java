package uniandes.edu.co.proyecto.controller;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import uniandes.edu.co.proyecto.modelo.CredencialesCuenta;
import uniandes.edu.co.proyecto.modelo.Cuenta;
import uniandes.edu.co.proyecto.modelo.Oficina;
import uniandes.edu.co.proyecto.modelo.Prestamo;
import uniandes.edu.co.proyecto.modelo.PuntosAtencion;
import uniandes.edu.co.proyecto.modelo.Usuario;
import uniandes.edu.co.proyecto.repositorio.CredencialesCuentaRepository;
import uniandes.edu.co.proyecto.repositorio.CuentaRepository;
import uniandes.edu.co.proyecto.repositorio.EstadoCuentaRepository;
import uniandes.edu.co.proyecto.repositorio.OficinaRepository;
import uniandes.edu.co.proyecto.repositorio.TipoCuentaRepository;
import uniandes.edu.co.proyecto.repositorio.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CuentaController {
    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CredencialesCuentaRepository credencialesCuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoCuentaRepository tipoCuentaRepository;

    @Autowired
    private EstadoCuentaRepository estadoCuentaRepository;
    private static final Logger logger = LoggerFactory.getLogger(CuentaController.class);

    // , Date fechaUltimoMov 
    @GetMapping("/cuenta")
    public String listarOficina(Model model, String tipoCuenta, Float saldoMin, Float saldoMax, Date ultimoMov,Date fechaCreacion) {
        model.addAttribute("tipoCuenta", tipoCuentaRepository.darTiposCuenta());
        if (tipoCuenta != null && saldoMin != null && saldoMax != null && ultimoMov != null) {
            model.addAttribute("cuentas", cuentaRepository.busquedaAvanzada(tipoCuenta, saldoMin, saldoMax,ultimoMov));
            return "cuenta";
        } 
  
        else if((tipoCuenta != null))
        {
            model.addAttribute("cuentas", cuentaRepository.cuentasPorTipo(tipoCuenta));
            return "cuenta";
        }

        else if((saldoMin != null && saldoMax !=null))
        {
            model.addAttribute("cuentas", cuentaRepository.cuentaSaldoRango(saldoMin, saldoMax));
            return "cuenta";
        }


        else if((ultimoMov!= null ))
        {
            model.addAttribute("cuentas", cuentaRepository.cuentasFechaUltimoMov(ultimoMov));
            return "cuenta";
        }

        else if((fechaCreacion!= null ))
        {
            model.addAttribute("cuentas", cuentaRepository.cuentasFechaCreacion(fechaCreacion));
            return "cuenta";
        }

        model.addAttribute("cuentas", cuentaRepository.busquedaAvanzada(tipoCuenta, saldoMin, saldoMax,ultimoMov));
          
        return "cuenta";
    }


    @GetMapping("/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas")
    public String listaCuentasSesionIniciada(Model model,@PathVariable("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.buscarUsuarioId(idUsuario);
        if ((usuario.getTipoUsuario().getTipoUsuario().equals("GERENTE DE OFICINA")) || (usuario.getTipoUsuario().getTipoUsuario().equals("GERENTE GENERAL"))){
            model.addAttribute("cuentas", cuentaRepository.darCuentas());
            model.addAttribute("idUsuario", idUsuario);
            return "cuenta";
        }
        else if ((usuario.getTipoUsuario().getTipoUsuario().equals("CLIENTE JURIDICO")) || (usuario.getTipoUsuario().getTipoUsuario().equals("CLIENTE NATURAL"))){
            Collection<Integer> idCuentas = credencialesCuentaRepository.buscarCredencialesCuentaPorIdUsuario(idUsuario);
            Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
            for (Integer idCuenta:idCuentas){
                Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
                cuentas.add(cuenta);
            }
            
            model.addAttribute("cuentasUsuario", cuentas);
            model.addAttribute("idUsuario", idUsuario);
            return "Usuariocuenta";
        }
        else{
            redirectAttributes.addFlashAttribute("noPermisos", "No tienes permiso para ver esta página.");
            return "redirect:/login_usuario/verificacionLogin/" + idUsuario;
        }
        
    }

    @GetMapping("/cuenta/new")
    public String formularioNuevoCuenta(Model model) {
        model.addAttribute("cuentas", new Cuenta());
        return "CuentaNuevo";
    }
    @PostMapping("/cuenta/new/save")
    public String guardarCuenta(@ModelAttribute("tipo_Cuenta") String tipoCuenta,@ModelAttribute("estado_cuenta") String estadoCuenta,@ModelAttribute("saldo") String saldo, Date fechaUltimaTransaccion) {
       
        //java.sql.Date fechaUltimaTransaccion = new java.sql.Date(fechaUltimaTransaccion);


        double saldoNumero = Double.parseDouble(saldo);
        
        cuentaRepository.insertarCuenta(tipoCuenta, estadoCuenta, saldoNumero, fechaUltimaTransaccion);
        
        return "redirect:/cuenta";
    }

    @GetMapping("/cuenta/{id_cuenta}/{idUsuario}/desactivar_Cuenta")
    public String desactivarCuenta(@PathVariable("id_cuenta") Integer idCuenta, @PathVariable("idUsuario") Integer idGerente, Model model,RedirectAttributes redirectAttributes) {
        
        CredencialesCuenta credencialesCuenta = credencialesCuentaRepository.buscarCredencialesCuentaPorIdCuenta(idCuenta);
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        if(credencialesCuenta.getGerente().getId().equals(idGerente)){
            if(cuenta.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")){
                cuentaRepository.cambiarEstadoDesactivada(idCuenta);
                return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
            }
            else{
                redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de cuenta es diferente a ACTIVA");
                return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
            }
        }
        else{
            redirectAttributes.addFlashAttribute("errorGerente", "El gerente no es el mismo que intenta hacer la modificacion");
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
        }
        
    }

    @GetMapping("/cuenta/{id_cuenta}/{idUsuario}/cerrar_cuenta")
    public String cerrarCuenta(@PathVariable("id_cuenta") Integer idCuenta, @PathVariable("idUsuario") Integer idGerente, Model model,RedirectAttributes redirectAttributes) {
        CredencialesCuenta credencialesCuenta = credencialesCuentaRepository.buscarCredencialesCuentaPorIdCuenta(idCuenta);
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        if(credencialesCuenta.getGerente().getId().equals(idGerente)){
            if(cuenta.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")){
                if (cuenta.getSaldo() == 0){
                    cuentaRepository.cambiarEstadoCerrada(idCuenta);
                }
                else{
                    redirectAttributes.addFlashAttribute("errorSaldo", "El saldo de la cuenta es diferente a 0");
                }
            }
            else{
                redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de cuenta es diferente a ACTIVA");
            }
        }
        else{
            redirectAttributes.addFlashAttribute("errorGerente", "El gerente no es el mismo que intenta hacer la modificacion");
        }
        System.out.println("entro");
        return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
    }

    @GetMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_retirar")
    public String cuentaRetirar(@PathVariable("id_cuenta") Integer idCuenta, @PathVariable("idUsuario") Integer idGerente, Model model) {
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        if (cuenta != null) {
            model.addAttribute("cuenta", cuenta);
            return "cuentaRetirar";
        }          
         else {
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
    }
    } 
    
    @GetMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_consignar")
    public String cuentaConsignar(@PathVariable("id_cuenta") Integer idCuenta,@PathVariable("idUsuario") Integer idUsuario, Model model) {
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        if (cuenta != null) {
            model.addAttribute("cuenta", cuenta);
            return "cuentaConsignar";
        }          
         else {
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
    }
    }  

    @PostMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_retirar/save")
    public String retirarCuentaGuardar(@PathVariable("idUsuario") Integer idUsuario,@PathVariable("id_cuenta") Integer idCuenta, @RequestParam("monto") String monto, RedirectAttributes redirectAttributes) {
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        Double saldo = cuenta.getSaldo();
        Double montoFloat = Double.parseDouble(monto);
        Double montoFinal = cuenta.getSaldo()-montoFloat;
        if (cuenta.getSaldo() > montoFloat){
            if(cuenta.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")){
                cuentaRepository.cambiarSaldo(idCuenta,montoFinal);
                logger.info("Fecha: {}, Numero de cuenta: {}, Monto: {}, Tipo de operacion: retiro, Saldo: {}",
                    LocalDate.now(), idCuenta, monto, saldo);
            }
            else{
                redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de cuenta es diferente a activa");
            }
        }
        else{
            redirectAttributes.addFlashAttribute("errorSaldo", "El saldo es menor al que pretendes retirar");
        }
        return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
    }

    @PostMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_consignar/save")
    public String consignarCuentaGuardar(@PathVariable("id_cuenta") Integer idCuenta,@PathVariable("idUsuario") Integer idUsuario, @RequestParam("monto") String monto,RedirectAttributes redirectAttributes) {
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        Double montoFloat = Double.parseDouble(monto);
        Double montoFinal = cuenta.getSaldo()+montoFloat;
        Double saldo = cuenta.getSaldo();
        if(cuenta.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")){
            cuentaRepository.cambiarSaldo(idCuenta,montoFinal);
            logger.info("Fecha: {}, Numero de cuenta: {}, Monto: {}, Tipo de operacion: rertiro, Saldo: {}",
                LocalDate.now(), idCuenta, monto, saldo);
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
        }
        else{
            redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de cuenta es diferente a activa");
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
        }
        
}

@GetMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_transferir")
    public String cuentaTransferir(@PathVariable("id_cuenta") Integer idCuenta,@PathVariable("idUsuario") Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Cuenta cuenta = cuentaRepository.buscarCuentaPorId(idCuenta);
        Collection<Cuenta> cuentasTransferir = cuentaRepository.darCuentasActivas(idCuenta,"ACTIVA");
        if (cuenta != null && cuenta.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")) {
            model.addAttribute("cuenta", cuenta);
            model.addAttribute("cuentasTransferir", cuentasTransferir);
            return "cuentaTransferir";
        } 
        if (cuenta != null && (cuenta.getEstadoCuenta().getEstadoCuenta().equals("CERRADA")|| cuenta.getEstadoCuenta().getEstadoCuenta().equals("DESACTIVADA"))) {
            redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de cuenta es diferente a activa");
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
        }          
         else {
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
    }
    }

@PostMapping("/cuenta/{id_cuenta}/{idUsuario}/cuenta_transferir/save")
    public String transferirCuentaGuardar(@PathVariable("id_cuenta") Integer idCuenta,@PathVariable("idUsuario") Integer idUsuario, @RequestParam("cuentaDestino") Integer idCuentaDestino,@RequestParam("monto") String monto,RedirectAttributes redirectAttributes) {
        Cuenta cuentaOrigen = cuentaRepository.buscarCuentaPorId(idCuenta);
        Cuenta cuentaDestino = cuentaRepository.buscarCuentaPorId(idCuentaDestino);
        double montoDouble = Double.parseDouble(monto);
        Double saldoO = cuentaOrigen.getSaldo();
        Double saldoD = cuentaDestino.getSaldo();
        
        if(cuentaOrigen.getSaldo() > montoDouble){
            if (cuentaOrigen.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA") && cuentaDestino.getEstadoCuenta().getEstadoCuenta().equals("ACTIVA")){
                double saldoNuevoOrigen = cuentaOrigen.getSaldo() - montoDouble;
                double saldoNuevoDestino = cuentaDestino.getSaldo() + montoDouble;
                logger.info("Fecha: {}, Numero de cuenta origen: {},Numero de cuenta destino: {}, Monto: {}, Tipo de operación: transferencia, Saldo cuenta origen: {}, Saldo cuenta destino: {}",
                LocalDate.now(), cuentaOrigen.getIdCuenta(), cuentaDestino.getIdCuenta(), montoDouble, saldoO, saldoD);
                cuentaOrigen.setSaldo(saldoNuevoOrigen);
                cuentaDestino.setSaldo(saldoNuevoDestino);
                return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
            }
            else{
                redirectAttributes.addFlashAttribute("errorEstadoCuenta", "El estado de tu cuenta es distinto a activa");
                return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
            }

        }
        else{
            redirectAttributes.addFlashAttribute("errorSaldoCuenta", "El Saldo de tu cuenta es menor al que quieres transferir");
            return "redirect:/{idUsuario}/gerenteoficina/cuentagerenteoficina/lista_cuentas";
        }
}


@GetMapping("{idUsuario}/gerenteoficina/cuentagerenteoficina")
public String listarCuentasGerente(Model model,@PathVariable("idUsuario") Integer idUsuario) {
    Collection<Integer> cuentas = credencialesCuentaRepository.darCuentasGerente(idUsuario);
    Collection<Cuenta> cuentasFinales = new ArrayList<>();
    for (Integer cuenta : cuentas) {
        cuentasFinales.add(cuentaRepository.buscarCuentaPorId(cuenta));}
    model.addAttribute("cuentas", cuentasFinales);

    return "cuentagerenteoficina";
}

@GetMapping("{idUsuario}/gerenteoficina/cuentagerenteoficina/cuentanuevogerente/new")
public String formularioNuevoCuentaGerenteOficina(@PathVariable("idUsuario") Integer idUsuario,Model model) {
    model.addAttribute("tipoCuentas", tipoCuentaRepository.darTiposCuenta());
    model.addAttribute("estadoCuentas", estadoCuentaRepository.darEstadoCuenta("ACTIVA"));
    model.addAttribute("clientes", usuarioRepository.darListaUsuarios("CLIENTE NATURAL", "CLIENTE JURIDICO"));
    model.addAttribute("idGerente", idUsuario);
    model.addAttribute("cuenta", new Cuenta());
    model.addAttribute("credenciales", new CredencialesCuenta());
    return "cuentanuevogerente";
}

@PostMapping("/{idUsuario}/gerenteoficina/cuentagerenteoficina/cuentanuevogerente/new/save")
public String guardarCuentaGerenteDeOficina(@ModelAttribute Cuenta cuenta, @ModelAttribute CredencialesCuenta credenciales, @PathVariable("idUsuario") Integer idGerente,@RequestParam("tipoCuenta") String tipoCuenta,@RequestParam("estadoCuenta") String estadoCuenta,@RequestParam("saldo") String saldo,Date fechaUltimaTransaccion,@RequestParam("idUsuario") Integer idCliente){
    double saldoDouble = Double.parseDouble(saldo);
    //fechaUltimaTransaccion = new java.sql.Date(System.currentTimeMillis());
    cuentaRepository.insertarCuenta(tipoCuenta,estadoCuenta,saldoDouble,fechaUltimaTransaccion);
    Integer idCuenta = cuentaRepository.DarIdMaximo();
    credencialesCuentaRepository.insertarCredencialesCuenta(idCliente, idGerente, idCuenta, fechaUltimaTransaccion);;
    return "sesionIniciada";
}


@GetMapping("/login_usuario/verificacionLogin/{idUsuario}/cuenta/movimientosCuenta")
public String consultarMovimientos(Model model, Integer idCuenta) {
    if (idCuenta != null) {
        String id = idCuenta.toString();
        String filePath = "logs/cuentas.log";
        try {
            Collection<String> lineasCoincidentes = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)
                    .filter(linea -> linea.contains(id))
                    .collect(Collectors.toList());
            
            if (!lineasCoincidentes.isEmpty()) {
                model.addAttribute("lineasCoincidentes", lineasCoincidentes);
            } else {
                model.addAttribute("resultado", "No se encontraron movimientos para la cuenta");
            }
        } catch (IOException e) {
            model.addAttribute("cuentas", cuentaRepository.darCuentas());
            return "movimientosCuenta";
        }
    }
    
    model.addAttribute("cuentas", cuentaRepository.darCuentas());
    return "movimientosCuenta";
}

@GetMapping("/extractosCuenta")
public String extractosGeneral(Model model, Integer idCuenta, Integer mes) {
    if (idCuenta != null && mes != null) {
        Collection<Integer> meses = IntStream.rangeClosed(1, 12)
                                            .boxed()
                                            .collect(Collectors.toList());
        String id = idCuenta.toString();
        String mesStr = String.format("%02d", mes); // Asegurar que el mes tenga dos dígitos
        String filePath = "logs/cuentas.log";
        try {
            Collection<String> lineasCoincidentes = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)
                    .filter(linea -> linea.contains(id) && linea.contains("-" + mesStr + "-"))
                    .collect(Collectors.toList());
            
            if (!lineasCoincidentes.isEmpty()) {
                model.addAttribute("lineasCoincidentes", lineasCoincidentes);
            } else {
                model.addAttribute("resultado", "No se encontraron movimientos para la cuenta en el mes especificado");
            }
        } catch (IOException e) {
            model.addAttribute("cuentas", cuentaRepository.darCuentas());
            return "movimientosCuenta";
        }
    }
    
    model.addAttribute("cuentas", cuentaRepository.darCuentas());
    return "movimientosCuenta";
}



}
