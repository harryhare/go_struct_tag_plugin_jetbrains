import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;


public class GenerateBothAction extends AnAction {
    public GenerateBothAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        //Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());
        Project project = event.getProject();
        if(project==null) {
            return;
        }
        System.out.println(project.getName());
        if(event.getData(PlatformDataKeys.EDITOR)==null) {
            return;
        }
        Document document= null;
        try {
            document = event.getData(PlatformDataKeys.EDITOR).getDocument();
        } catch (NullPointerException e) {
            return;
        }
        String origin=document.getText();
        String modified = TagAdder.ModifyTag(origin,true,true);
        document.setText(modified);
    }
}