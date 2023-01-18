package com.sil.sil_synchronizer.Services;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Variables;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataExtraccionRequest;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataExtraccionResponse;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataExtraccionSubterranea;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataExtraccionTrazaType;
import com.sil.sil_synchronizer.webservices.wsdl.AuthSendDataUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DgaClientService extends WebServiceGatewaySupport {

    @Value("${dga.login.username}")
    private String dgaUser;

    @Value("${dga.login.password}")
    private String dgaPassword;

    @Value("${dga.service.url}")
    private String dgaServiceUrl;

    @Value("${dga.wsld.url}")
    private String dgaWsdlUrl;

    @Autowired
    Variables variables;

    DateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");

    DateFormat dateFormatDate = new SimpleDateFormat("dd-MM-yyyy");

    public AuthSendDataExtraccionResponse sendDataExtrationToDga(DgaRequiredInformationDto dgaRequiredInformationDto) throws Exception {

        log.info("Sending object to DGA: {}", dgaRequiredInformationDto);

        //Prepare request
        AuthSendDataExtraccionRequest request = new AuthSendDataExtraccionRequest();

        //Prepare login data init
        AuthSendDataUsuario dataUsuario = new AuthSendDataUsuario();
        //  User
        AuthSendDataUsuario.IdUsuario idUsuario = new AuthSendDataUsuario.IdUsuario();
        idUsuario.setRut(dgaUser);
        dataUsuario.setIdUsuario(idUsuario);
        //  Pass
        dataUsuario.setPassword(dgaPassword);

        //Prepare extraction data
        AuthSendDataExtraccionSubterranea extraccionSubterranea = new AuthSendDataExtraccionSubterranea();
        // Info
        extraccionSubterranea.setCaudal(BigDecimal.valueOf(dgaRequiredInformationDto.getFlow()));
        extraccionSubterranea.setTotalizador(BigDecimal.valueOf(dgaRequiredInformationDto.getTotalizer()));
        extraccionSubterranea.setNivelFreaticoDelPozo(BigDecimal.valueOf(dgaRequiredInformationDto.getPhreaticLevel()));
        extraccionSubterranea.setHoraMedicion(dateFormatTime.format(dgaRequiredInformationDto.getDate()));
        extraccionSubterranea.setFechaMedicion(dateFormatDate.format(dgaRequiredInformationDto.getDate()));

        //Prepare headers
        AuthSendDataExtraccionTrazaType extraccionTrazaType = new AuthSendDataExtraccionTrazaType();
        extraccionTrazaType.setCodigoDeLaObra(dgaRequiredInformationDto.getSiteCode());
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        extraccionTrazaType.setTimeStampOrigen(DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED));

        //Fill request auth data
        request.setAuthDataUsuario(dataUsuario);
        //Fill request extraction data
        request.setAuthDataExtraccionSubterranea(extraccionSubterranea);

        AuthSendDataExtraccionResponse response = (AuthSendDataExtraccionResponse) getWebServiceTemplate().marshalSendAndReceive(dgaWsdlUrl, request, new SoapActionCallback(dgaServiceUrl));

        if (response.getStatus().getCode().equals("0")) {
            log.info("Información enviada exitosamente");
        } else {
            log.error("Ha ocurrido el siguiente error al enviar la información: {}", response.getStatus().getDescription());
            throw new Exception(response.getStatus().getDescription());
        }

        //Set the min wait between every DGA endpoint call
        TimeUnit.SECONDS.sleep(variables.getDgaWebServiceSecondsDelay());

        return response;
    }
}
