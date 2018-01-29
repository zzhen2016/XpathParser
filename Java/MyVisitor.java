package src;

import org.w3c.dom.*;

import java.util.*;

public class MyVisitor extends HelloBaseVisitor<List<Context>> {

    Stack<List<Context>> stack;

    @Override
    public List<Context> visitApSingleSlash(HelloParser.ApSingleSlashContext ctx) {
        stack=new Stack<List<Context>>();
        Document doc=XMLDocument.CreateRoot(ctx.Letter().getText());
        List<Context> temp=new LinkedList<Context>();
        temp.add(new Context(doc));
        stack.push(temp);
        return visit(ctx.rp());
    }

    @Override
    public List<Context> visitApDoubleSlash(HelloParser.ApDoubleSlashContext ctx) {
        stack=new Stack<List<Context>>();
        Document doc=XMLDocument.CreateRoot(ctx.Letter().getText());
        List<Context> res=new LinkedList<Context>();
        Context context=new Context(doc);
        res.add(context);
        res.addAll(context.getdecendants());
        List unique_res=unique(res);
        stack.push(unique_res);
        return visit(ctx.rp());
    }

    @Override
    public List<Context> visitRpTagName(HelloParser.RpTagNameContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            res.addAll(peek.get(i).gettagname(ctx.Letter().getText()));
        }
        stack.push(res);
        return res;
    }


    @Override
    public List<Context> visitRpAllChildren(HelloParser.RpAllChildrenContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            res.addAll(peek.get(i).getchildren());
        }
        stack.push(res);
        return res;
    }

    @Override
    public List<Context> visitRpCurrent(HelloParser.RpCurrentContext ctx) {
        return stack.peek();
    }

    @Override
    public List<Context> visitRpParent(HelloParser.RpParentContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            Context value=peek.get(i).getparent();
            if(value!=null) res.add(value);
        }
        List unique_res=unique(res);
        stack.push(unique_res);
        return unique_res;
    }

    public List<Context> unique(List<Context> input){
        Set<Node> set=new HashSet<>();
        List<Context> out=new LinkedList<>();
        for(int i=0;i<input.size();i++){
            Node node_test=input.get(i).node;
            if(set.contains(node_test)) continue;
            set.add(node_test);
            out.add(input.get(i));
        }
        return out;
    }

    @Override
    public List<Context> visitRpGetTextNode(HelloParser.RpGetTextNodeContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            res.addAll(peek.get(i).gettextnode());
        }
        stack.push(res);
        return res;
    }

    @Override
    public List<Context> visitRpGetAttribute(HelloParser.RpGetAttributeContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            if(peek.get(i).node.getAttributes()!=null){
                Context value=peek.get(i).getatt(ctx.Letter().getText());
                if(value!=null) res.add(value);
            }
        }
        stack.push(res);
        return res;
    }

    @Override
    public List<Context> visitRpAllAttribute(HelloParser.RpAllAttributeContext ctx) {
        List<Context> res=new LinkedList<Context>();
        List<Context> peek=stack.pop();
        for(int i=0;i<peek.size();i++){
            if(peek.get(i).node.getAttributes()!=null){
                List<Context> value=peek.get(i).getallatt();
                res.addAll(value);
            }
        }
        stack.push(res);
        return res;
    }

    @Override
    public List<Context> visitRpParenthesis(HelloParser.RpParenthesisContext ctx) {
        return visit(ctx.rp());
    }

    @Override
    public List<Context> visitRpSingleSlash(HelloParser.RpSingleSlashContext ctx) {
        visit(ctx.rp(0));
        return visit(ctx.rp(1));
    }

    @Override
    public List<Context> visitRpDoubleSlash(HelloParser.RpDoubleSlashContext ctx) {
        visit(ctx.rp(0));
        List<Context> rp1_result=stack.pop();
        List<Context> res=new LinkedList<>();
        for(int i=0;i<rp1_result.size();i++){
            res.addAll(rp1_result.get(i).getdecendants());
        }
        List unique_res=unique(res);
        stack.push(unique_res);
        return visit(ctx.rp(1));
    }


    @Override
    public List<Context> visitRpQuote(HelloParser.RpQuoteContext ctx) {
        List<Context> store_context=stack.peek();
        List<Context> concat=new LinkedList<>();
        List<Context> rp1_result=visit(ctx.rp(0));
        stack.pop();
        stack.push(store_context);
        List<Context> rp2_result=visit(ctx.rp(1));
        stack.pop();
        concat.addAll(rp1_result);
        concat.addAll(rp2_result);
        List unique_res=unique(concat);  //make two combined list unique
        stack.push(unique_res);
        return unique_res;
    }

    @Override
    public List<Context> visitRpFilter(HelloParser.RpFilterContext ctx) {
        visit(ctx.rp());
        return visit(ctx.f());
    }

    @Override
    public List<Context> visitFilterRp(HelloParser.FilterRpContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result=visit(ctx.rp());
            stack.pop();
            if(each_result.size()!=0) filter_res.add(test_context.get(i));  //check if the node satisfy the filter condition
        }
        stack.push(filter_res);
        return filter_res;
    }

    @Override
    public List<Context> visitFilterindex(HelloParser.FilterindexContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        Integer pos=Integer.parseInt(ctx.NUM().getText());  //get that specific index from the node list
        if(pos>=test_context.size()) return filter_res;
        filter_res.add(test_context.get(pos));
        stack.push(filter_res);
        return filter_res;
    }

    @Override
    public List<Context> visitFilterAttribute(HelloParser.FilterAttributeContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result=visit(ctx.rp());
            stack.pop();
            if(each_result.size()==0) continue;  // handle no such attribute for that node
            if(check_attribute_node(each_result,ctx.Letter().getText())) filter_res.add(test_context.get(i));
        }
        stack.push(filter_res);
        return filter_res;
    }

    //check if a element node has a attribute node with specific value

    public boolean check_attribute_node(List<Context> list, String attributevalue){
        for(int i=0;i<list.size();i++){
            Node each_Attribute_node=list.get(i).node;
            if(each_Attribute_node.getNodeValue().equals(attributevalue)) return true;
        }
        return false;
    }

    @Override
    public List<Context> visitFilterEq(HelloParser.FilterEqContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result_1=visit(ctx.rp(0));  //get the first filter list
            stack.pop();
            stack.push(each_test);
            List<Context> each_result_2=visit(ctx.rp(1));  //get the second filter list
            stack.pop();
            if(check_if_satisfy(each_result_1,each_result_2)) filter_res.add(test_context.get(i));  //check if there exists two structure identical nodes in two list
        }
        stack.push(filter_res);
        return filter_res;
    }

    public boolean check_if_satisfy(List<Context> l1, List<Context> l2){
        for(int i=0;i<l1.size();i++){
            for(int j=0;j<l2.size();j++){
                Node root1=l1.get(i).node;
                Node root2=l2.get(i).node;
                if(is_equal(root1,root2)) return true;
            }
        }
        return false;  //what if two lists are empty?
    }

    // check if two nodes are structure identical

    public boolean is_equal(Node root1,Node root2){
        if(root1.getNodeType()!= root2.getNodeType()||root1.getChildNodes().getLength()!=root2.getChildNodes().getLength()) return false;
        if(root1.getNodeType()== Node.TEXT_NODE){
            if(root1.getTextContent()!= root2.getTextContent()) return false;
        }
        if(root1.getNodeType()== Node.ELEMENT_NODE){
            if(!root1.getNodeName().equals(root2.getNodeName())) return false;
        }
        for(int i=0;i<root1.getChildNodes().getLength();i++){
            Node test1=root1.getChildNodes().item(i);
            Node test2=root2.getChildNodes().item(i);
            if(!is_equal(test1,test2)) return false;
        }
        return true;
    }

    @Override
    public List<Context> visitFilterIs(HelloParser.FilterIsContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result_1=visit(ctx.rp(0));
            stack.pop();
            stack.push(each_test);
            List<Context> each_result_2=visit(ctx.rp(1));
            stack.pop();
            if(check_if_identical(each_result_1,each_result_2)) filter_res.add(test_context.get(i));  //check if there exists a node in both list
        }
        stack.push(filter_res);
        return filter_res;
    }

    //check if there exists a node in both list

    public boolean check_if_identical(List<Context> l1, List<Context> l2){
        for(int i=0;i<l1.size();i++){
            for(int j=0;j<l2.size();j++){
                Node root1=l1.get(i).node;
                Node root2=l2.get(i).node;
                if(root1==root2) return true;
            }
        }
        return false;
    }

    @Override
    public List<Context> visitFilterQuote(HelloParser.FilterQuoteContext ctx) {
        return visit(ctx.f());
    }

    @Override
    public List<Context> visitFilterAnd(HelloParser.FilterAndContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result_1=visit(ctx.f(0));
            stack.pop();
            stack.push(each_test);
            List<Context> each_result_2=visit(ctx.f(1));
            stack.pop();
            if(each_result_1.size()!=0&&each_result_2.size()!=0) filter_res.add(test_context.get(i));  //check if the node satisfy two filter conditions
        }
        stack.push(filter_res);
        return filter_res;
    }

    @Override
    public List<Context> visitFilterOr(HelloParser.FilterOrContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result_1=visit(ctx.f(0));
            stack.pop();
            stack.push(each_test);
            List<Context> each_result_2=visit(ctx.f(1));
            stack.pop();
            if(each_result_1.size()!=0||each_result_2.size()!=0) filter_res.add(test_context.get(i));  //check is the node satisfy one of filter conditions
        }
        stack.push(filter_res);
        return filter_res;
    }

    @Override
    public List<Context> visitFilterNot(HelloParser.FilterNotContext ctx) {
        List<Context> test_context=stack.pop();
        List<Context> filter_res=new LinkedList<>();
        for(int i=0;i<test_context.size();i++){
            List<Context> each_test=new LinkedList<>();
            each_test.add(test_context.get(i));
            stack.push(each_test);
            List<Context> each_result=visit(ctx.f());
            if(each_result.size()==0) filter_res.add(test_context.get(i));  //check if the node unsatisfy the filter condition. if it was, then we want it.
        }
        stack.push(filter_res);
        return filter_res;
    }
}
