package inspections;

import checks.ClassIsFinalCheck;
import checks.ClassIsSynchronizedCheck;
import checks.ClassUseIncompatibleMethodsCheck;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import export.ExportToTxtFileWriter;
import export.ExportWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.ReplaceWithInlineFix;
import quickFixes.ReplaceWithRecordFix;

import static startup.activity.WriteToFileStartupActivity.enableWrite;

public class AllElementsAreFinalinspection extends AbstractBaseJavaLocalInspectionTool {

    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private ClassIsFinalCheck isFinalCheck = new ClassIsFinalCheck();
    private ClassUseIncompatibleMethodsCheck classUseIncompatibleMethodsCheck = new ClassUseIncompatibleMethodsCheck();
    private ExportWriter fileWriter;

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (isFinalCheck.checkClass(aClass) &&
                isFinalCheck.checkFields(aClass.getAllFields()) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods()) &&
                !classUseIncompatibleMethodsCheck.checkClass(aClass)) {
            if (enableWrite == true) {
                if (fileWriter == null) {
                    this.fileWriter = new ExportToTxtFileWriter(aClass.getProject().getBasePath());
                }
                fileWriter.export(aClass.getName());
            }
            PsiFile file = aClass.getContainingFile();
            ProblemsHolder holder = new ProblemsHolder(manager, file, isOnTheFly);
            holder.registerProblem(aClass, "Class is candidate for record/inline",
                    ProblemHighlightType.WARNING,
                    new ReplaceWithRecordFix(aClass), new ReplaceWithInlineFix(aClass));
            return holder.getResultsArray();
        }
        return ProblemDescriptor.EMPTY_ARRAY;
    }
}
