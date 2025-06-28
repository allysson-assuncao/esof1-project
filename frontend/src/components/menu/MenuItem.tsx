import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { HierarchicalCategoryDTO, ProductDTO } from '@/model/Interfaces';
import { fetchProductsByCategoryId} from "@/services/menuService";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"; // Usando um componente da shadcn/ui

interface MenuItemProps {
    category: HierarchicalCategoryDTO;
}

export const MenuItem: React.FC<MenuItemProps> = ({ category }) => {
    console.log("Renderizando MenuItem para:", category);
    const [isOpen, setIsOpen] = useState(false);

    const hasSubcategories = category.subCategories && category.subCategories.length > 0;

    // Busca os produtos para esta categoria APENAS quando o item for aberto E não tiver subcategorias
    const { data: products, isLoading } = useQuery<ProductDTO[]>(
        ['products', category.id],
        () => fetchProductsByCategoryId(category.id),
        {
            enabled: isOpen && !hasSubcategories, // SÓ EXECUTA A QUERY QUANDO NECESSÁRIO!
        }
    );

    return (
        <Collapsible open={isOpen} onOpenChange={setIsOpen} className="pl-4">
            <CollapsibleTrigger className="w-full text-left py-2 flex justify-between items-center">
                <span>{category.name}</span>
                {/* Mostra um ícone de "maior que" ou similar */}
                <span>&gt;</span>
            </CollapsibleTrigger>
            <CollapsibleContent>
                {/* Se tem subcategorias, renderiza elas */}
                {hasSubcategories && (
                    <div>
                        {category.subCategories.map(subCat => (
                            <MenuItem key={subCat.id} category={subCat} />
                        ))}
                    </div>
                )}

                {/* Se NÃO tem subcategorias, mostra os produtos */}
                {!hasSubcategories && isLoading && <p>Carregando produtos...</p>}
                {!hasSubcategories && products && (
                    <div className="pl-6">
                        {products.map(product => (
                            <div key={product.id} className="py-1">
                                <p className="font-semibold">{product.name} - R$ {product.price}</p>
                                <p className="text-sm text-gray-500">{product.description}</p>
                            </div>
                        ))}
                    </div>
                )}
            </CollapsibleContent>
        </Collapsible>
    );
};