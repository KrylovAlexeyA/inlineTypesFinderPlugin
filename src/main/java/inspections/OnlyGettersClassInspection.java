package inspections;

import checks.ClassIsSynchronizedCheck;
import checks.ClassUseIncompatibleMethodsCheck;
import checks.MethodsOfObjectCheck;
import checks.NoModifiedFieldsCheck;
import com.intellij.codeInspection.*;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import export.ExportToTxtFileWriter;
import export.ExportWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quickFixes.ReplaceWithInlineFix;
import quickFixes.ReplaceWithRecordFix;

import static quickFixes.ReplaceWithInlineFix.InlineAnnotation;
import static startup.activity.WriteToFileStartupActivity.enableWrite;

public class OnlyGettersClassInspection extends AbstractBaseJavaLocalInspectionTool {

    private MethodsOfObjectCheck inheritedFromObjectCheck = new MethodsOfObjectCheck();
    private ClassIsSynchronizedCheck isSynchronizedCheck = new ClassIsSynchronizedCheck();
    private NoModifiedFieldsCheck noModifiedFieldsCheck = new NoModifiedFieldsCheck();
    private ClassUseIncompatibleMethodsCheck classUseIncompatibleMethodsCheck = new ClassUseIncompatibleMethodsCheck();
    private ExportWriter fileWriter;

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!aClass.isRecord() &&
                !aClass.hasAnnotation(InlineAnnotation) &&
                checkMethods(aClass.getAllMethods()) &&
                !isSynchronizedCheck.checkMethods(aClass.getAllMethods()) &&
                noModifiedFieldsCheck.checkClass(aClass) &&
                !isSynchronizedCheck.isBlockedBySynchronized(aClass) &&
                !classUseIncompatibleMethodsCheck.checkClass(aClass)) {
            if (enableWrite) {
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

    private boolean checkMethods(PsiMethod[] methods) {
        if (methods.length == 0) return false;
        long i = 0;
        for (PsiMethod method : methods) {
            boolean startsWithGet = false;
            if (method.getName().startsWith("get")) {
                startsWithGet = true;
                i++;
            }
            if (!startsWithGet && !method.isConstructor()) {
                if (inheritedFromObjectCheck.checkMethod(method) || method.hasModifier(JvmModifier.NATIVE)) {
                    continue;
                }
                return false;
            }
        }
        return i > 1;
    }
}
