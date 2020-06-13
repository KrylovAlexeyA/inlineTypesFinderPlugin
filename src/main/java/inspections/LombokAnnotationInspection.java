package inspections;

import checks.ClassIsSynchronizedCheck;
import checks.ClassUseIncompatibleMethodsCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.ReplaceWithInlineFix;
import quickFixes.ReplaceWithRecordFix;

public class LombokAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {
    public final static String VALUE_ANNOTATION = "lombok.Value";
    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private ClassUseIncompatibleMethodsCheck classUseIncompatibleMethodsCheck = new ClassUseIncompatibleMethodsCheck();


    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (aClass.hasAnnotation(VALUE_ANNOTATION) &&
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
