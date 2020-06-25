package inspections;

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

import static quickFixes.ReplaceWithInlineFix.InlineAnnotation;
import static startup.activity.WriteToFileStartupActivity.enableWrite;

public class LombokAnnotationInspection extends AbstractBaseJavaLocalInspectionTool {
    public final static String VALUE_ANNOTATION = "lombok.Value";
    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private ClassUseIncompatibleMethodsCheck classUseIncompatibleMethodsCheck = new ClassUseIncompatibleMethodsCheck();
    private ExportWriter fileWriter;

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!aClass.isRecord() &&
                !aClass.hasAnnotation(InlineAnnotation) &&
                aClass.hasAnnotation(VALUE_ANNOTATION) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods()) &&
                !isSynchronizedCheck.isBlockedBySynchronized(aClass) &&
                !classUseIncompatibleMethodsCheck.checkClass(aClass)) {
            if (enableWrite == true) {
                if (fileWriter == null) {
                    this.fileWriter = new ExportToTxtFileWriter(aClass.getProject().getBasePath());
                }
                fileWriter.export(aClass.getName());
            }
            fileWriter.export(aClass.getName());
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
