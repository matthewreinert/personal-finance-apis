package mjr.apps.personalfinanceapis.account;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("accounts")
public class Account {

    @Id
    private Long id;
    private String name;
}
