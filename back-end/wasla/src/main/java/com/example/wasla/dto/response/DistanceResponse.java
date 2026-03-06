package com.example.wasla.dto.response;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DistanceResponse {
    public String[] destination_addresses;
    public String[] origin_addresses;
    public Row[] rows;
    public String status;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Row {
        public Element[] elements;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Element {
        public Distance distance;
        public Duration duration;
        public String status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Distance {
        public String text; // e.g., "15.2 km"
        public long value;  // e.g., 15234 (in meters)
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Duration {
        public String text; // e.g., "22 mins"
        public long value;  // e.g., 1320 (in seconds)
    }
}

//Field,Example Value,Meaning
//origin_addresses,"[""Cairo Tower, Zamalek, Cairo, Egypt""]","The ""official"" address Google found."
//distance.text,"""5.4 km""",Human-friendly distance.
//distance.value,5420,"The ""Fair Logic"" number (meters)."
//duration.text,"""12 mins""",Estimated time in traffic.
//duration.value,720,Time in seconds.