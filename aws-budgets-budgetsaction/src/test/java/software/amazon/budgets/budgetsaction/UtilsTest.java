package software.amazon.budgets.budgetsaction;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {

    @Test
    public void convertEmptyDefinitionFromCfn() {
        Definition cfnDefinition = new Definition();
        software.amazon.awssdk.services.budgets.model.Definition sdkDefinition =
                Utils.convertDefinitionFromCfn(cfnDefinition);
        assertThat(sdkDefinition.iamActionDefinition()).isNull();
        assertThat(sdkDefinition.scpActionDefinition()).isNull();
        assertThat(sdkDefinition.ssmActionDefinition()).isNull();
    }

    @Test
    public void convertEmptyDefinitionFromSDK() {
        software.amazon.awssdk.services.budgets.model.Definition sdkDefinition =
                software.amazon.awssdk.services.budgets.model.Definition.builder().build();
        Definition cfnDefinition = Utils.convertDefinitionFromSdk(sdkDefinition);
        assertThat(cfnDefinition.getIamActionDefinition()).isNull();
        assertThat(cfnDefinition.getScpActionDefinition()).isNull();
        assertThat(cfnDefinition.getSsmActionDefinition()).isNull();
    }
}
