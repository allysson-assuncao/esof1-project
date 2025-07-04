// RegisterCategory.tsx
import {AddCategoryForm} from "@/components/form/add/AddCategoryForm";

export function RegisterCategory() {
    return (
        <div className="container w-full mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6 text-center">Nova Categoria</h1>
            <AddCategoryForm />
        </div>
    )
}
