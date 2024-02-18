package ru.timestop.video.generator.server.transcript.model;

import java.util.List;
import java.util.Map;

/**
 * @author t.i.m.e.s.t.o.p@mail.ru
 */
public record AudioTemplate(
        List<Integer> choosed,
        Map<String, Integer> mapping
) {
}
