package no.nav.pensjon.selvbetjeningopptjening.fullmakt.client;

import no.nav.pensjon.selvbetjeningopptjening.fullmakt.*;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.AktoerDto;
import no.nav.pensjon.selvbetjeningopptjening.fullmakt.client.dto.FullmaktDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.pensjon.selvbetjeningopptjening.util.DateUtil.asLocalDate;

public class FullmaktMapper {

     static List<Fullmakt> fullmakter(AktoerDto aktoer) {
        if (aktoer == null) {
            return emptyList();
        }

        List<FullmaktDto> fullmakter = new ArrayList<>();
        List<FullmaktDto> fullmakterTil = aktoer.fullmaktTil();
        List<FullmaktDto> fullmakterFra = aktoer.fullmaktFra();
        fullmakter.addAll(fullmakterTil == null ? emptyList() : fullmakterTil);
        fullmakter.addAll(fullmakterFra == null ? emptyList() : fullmakterFra);

        return fullmakter
                .stream()
                .map(FullmaktMapper::asFullmakt)
                .toList();
    }

    private static Fullmakt asFullmakt(FullmaktDto dto) {
        return new Fullmakt(
                dto.fullmaktId(),
                Fullmakttype.valueOf(dto.kodeFullmaktType()),
                Fullmaktnivaa.valueOf(dto.kodeFullmaktNiva()),
                asLocalDate(dto.fomDato()),
                asLocalDate(dto.tomDato()),
                dto.gyldig(),
                dto.versjon(),
                Fagomraade.valueOf(dto.fagomrade()),
                asAktoer(dto.aktorGir()),
                asAktoer(dto.aktorMottar()));
    }

    private static Aktoer asAktoer(AktoerDto dto) {
        return new Aktoer(
                dto.aktorNr(),
                dto.kodeAktorType(),
                asFullmakter(dto.fullmaktFra()),
                asFullmakter(dto.fullmaktTil()));
    }

    private static List<Fullmakt> asFullmakter(List<FullmaktDto> dtos) {
        return dtos == null
                ? emptyList()
                : dtos.stream().map(FullmaktMapper::asFullmakt).toList();
    }
}
