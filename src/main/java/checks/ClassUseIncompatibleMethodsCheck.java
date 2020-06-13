package checks;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ClassUseIncompatibleMethodsCheck {
    private final static Set<String> incompatibleMethods = Set.of(
            "notify",
            "notifyAll",
            "wait",
            "hashCode");

    public boolean checkClass(@NotNull PsiClass psiClass) {
        Query<PsiReference> references = ReferencesSearch.search(psiClass);
        if (!references.findAll().isEmpty()) {
            for (PsiReference reference : references) {
                PsiElement element = reference.getElement().getContext().getContext();
                if (element instanceof PsiVariable) {
                    Query<PsiReference> variableRefs = ReferencesSearch.search(element);
                    if (!variableRefs.findAll().isEmpty()) {
                        for (PsiReference variableRef : variableRefs.findAll()) {
                            if (incompatibleMethods.contains(variableRef.getElement().
                                    getContext().getLastChild().getText())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
