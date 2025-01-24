package pattern.command;

import model.Member;
import controller.MemberController;

public class AddMemberCommand implements Command {
    private MemberController controller;
    private Member member;
    
    public AddMemberCommand(MemberController controller, Member member) {
        this.controller = controller;
        this.member = member;
    }
    
    @Override
    public void execute() throws Exception {
        controller.saveMember(member);
    }
    
    @Override
    public void undo() throws Exception {
        controller.deleteMember(member.getId());
    }
}

public class DeleteMemberCommand implements Command {
    private MemberController controller;
    private Member member;
    
    public DeleteMemberCommand(MemberController controller, int memberId) {
        this.controller = controller;
        this.member = controller.findMemberById(memberId);
    }
    
    @Override
    public void execute() throws Exception {
        controller.deleteMember(member.getId());
    }
    
    @Override
    public void undo() throws Exception {
        controller.saveMember(member);
    }
}