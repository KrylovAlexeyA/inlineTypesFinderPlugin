package inspections;

import checks.ClassIsSynchronizedCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AllElementsIsFinalinspection extends AbstractBaseJavaLocalInspectionTool {

    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();


    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (aClass.hasModifierProperty(PsiModifier.FINAL) && allFieldsIsFinal(aClass.getFields()) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods())) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline", ProblemHighlightType.INFORMATION);
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }


    private boolean allFieldsIsFinal(@NotNull PsiField[] fields) {
        if (fields == null || fields.length > 0) {
            for (PsiField field : fields) {
                if (field.hasModifierProperty(PsiModifier.FINAL) == false) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
