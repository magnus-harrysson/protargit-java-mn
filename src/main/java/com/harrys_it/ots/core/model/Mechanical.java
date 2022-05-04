package com.harrys_it.ots.core.model;


import java.util.Objects;

public record Mechanical(
        Motor motor,
        Movement movement,
        TargetMode targetMode
) {}
