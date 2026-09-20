package escuela.admin.dto;

import java.util.Set;

public record AlcanceReporteFinanciero(boolean institucional, Set<Long> plantelIds) {
    public AlcanceReporteFinanciero {
        plantelIds = plantelIds == null || plantelIds.isEmpty() ? Set.of(-1L) : Set.copyOf(plantelIds);
    }
}
