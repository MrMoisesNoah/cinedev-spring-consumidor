package br.com.dbc.vemser.notascinedevconsumidor.service;

import br.com.dbc.vemser.notascinedevconsumidor.dto.NotasFiscaisCinemaDTO;
import br.com.dbc.vemser.notascinedevconsumidor.entity.NotaEntity;
import br.com.dbc.vemser.notascinedevconsumidor.repository.NotaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumidorService {
    private final ObjectMapper objectMapper;
    private final NotaRepository notaRepository;

    @Value(value = "${email.cinedev}")
    private String email;
    @KafkaListener(
            topics = "${kafka.topic}",
            clientIdPrefix = "{$spring.kafka.consumer.group-id}",
            groupId = "{$spring.kafka.consumer.group-id}",
            topicPartitions = {@TopicPartition(topic = "${kafka.topic}", partitions = {"0"})}
    )
    public void consumirGeral(@Payload String msg) throws JsonProcessingException {
        NotasFiscaisCinemaDTO notaFiscalRecebida = objectMapper.readValue(msg, NotasFiscaisCinemaDTO.class);
        NotaEntity nota = new NotaEntity();
        nota.setNomeCliente(notaFiscalRecebida.getNomeCliente());
        nota.setNomeFilme(notaFiscalRecebida.getNomeFilme());
        nota.setNomeCinema(notaFiscalRecebida.getNomeCinema());
        nota.setIdIngresso(notaFiscalRecebida.getIdIngresso());
        nota.setData(notaFiscalRecebida.getDataHora());
        nota.setQuantidade(1);
        nota.setPreco(notaFiscalRecebida.getPreco());


        notaRepository.save(nota);

    }

}