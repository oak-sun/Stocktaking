package nam.gor.stocktaking.domain;

import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class IdGenerator {


    public String newId() {
        return UUID.randomUUID().toString();
    }
}
