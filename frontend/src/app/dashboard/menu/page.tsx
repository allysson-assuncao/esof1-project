// Em src/app/menu/page.tsx

"use client";

import { useQuery } from 'react-query';
import { fetchMenuStructure } from '@/services/menuService';
import { MenuItem } from '@/components/menu/MenuItem'; // Verifique o caminho
import { HierarchicalCategoryDTO } from '@/model/Interfaces';

const MenuPage = () => {
    const { data: menuStructure, isLoading, error } = useQuery<HierarchicalCategoryDTO[]>('menuStructure', fetchMenuStructure);

    if (isLoading) return <div>Carregando Cardápio...</div>;
    if (error) return <div>Ocorreu um erro ao carregar o cardápio.</div>;

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-3xl font-bold mb-6">Nosso Cardápio</h1>
            <div>
                {menuStructure?.map(rootCategory => (
                    <MenuItem key={rootCategory.id} category={rootCategory} />
                ))}
            </div>
        </div>
    );
}

export default MenuPage;