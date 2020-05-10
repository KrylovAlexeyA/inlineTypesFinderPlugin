package checks;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MethodsOfObjectCheck {
    private final static Set<String> objectMethods = new HashSet<>(Arrays.asList(
            "getClass",
            "hashCode",
            "equals",
            "clone",
            "toString",
            "notify",
            "notifyAll",
            "wait",
            "finalize"));

    public boolean checkMethods(@NotNull PsiMethod method) {
        return objectMethods.contains(method.getName());
    }
}
