package inspections;

import checks.ClassIsFinalCheck;
import checks.ClassIsSynchronizedCheck;
import checks.ClassUseIncompatibleMethodsCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.ReplaceWithInlineFix;
import quickFixes.ReplaceWithRecordFix;

public class AllElementsAreFinalinspection extends AbstractBaseJavaLocalInspectionTool {

    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private ClassIsFinalCheck isFinalCheck = new ClassIsFinalCheck();
    private ClassUseIncompatibleMethodsCheck classUseIncompatibleMethodsCheck = new ClassUseIncompatibleMethodsCheck();


    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (isFinalCheck.checkClass(aClass) &&
                isFinalCheck.checkFields(aClass.getAllFields()) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods()) &&
                !classUseIncompatibleMethodsCheck.checkClass(aClass)) {
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline",
                    ProblemHighlightType.INFORMATION,
                    new ReplaceWithRecordFix(aClass), new ReplaceWithInlineFix(aClass));
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }
}
