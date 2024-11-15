import { useEffect, useState } from 'react'
import { GrDrag } from "react-icons/gr";
import { DragDropContext, Draggable } from "react-beautiful-dnd";
import { StrictModeDroppable } from '../components/StrictModeDroppable'
import VariantType from '../components/VariantType.js';
import useAlert from '../state/useAlert.js';
import { getProperty } from '../api/mypantry/purchase/purchaseService.js';
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';

export function CategoryDragDrop({ innitialList, handleListChange, disabled }) {

    const { t } = useTranslation(['supermarket', 'categories']);

    const { showAlert } = useAlert();

    const [filteredCategories, setFilteredCategories] = useState(populateFilteredCategories(innitialList));
    const [categories, setCategories] = useState([]);

    const lists = {
        droppableFiltered: filteredCategories,
        droppableSource: categories
    }

    useEffect(() => {
        fetchCategories();
    }, []);

    useEffect(() => {
        translateAndSortCategories();
        translateFilteredCategories();
    }, [i18n.language]);

    function populateFilteredCategories(list) {
        var filtered = [];
        list.forEach((category) => {
            filtered = [...filtered,
            {
                id: category,
                name: t(category, { ns: 'categories' })
            }
            ]
        });
        return filtered;
    }

    function translateAndSortCategories() {
        var sourceList = categories.map((item) =>
        (
            item = { ...item, name: t(item.id, { ns: 'categories' }) })
        );

        sourceList = sourceList.sort((a, b) => a.name.localeCompare(b.name));
        setCategories(sourceList);
    }

    function translateFilteredCategories() {
        const filtered = filteredCategories.map((item) =>
        (
            item = { ...item, name: t(item.id, { ns: 'categories' }) })
        );

        setFilteredCategories(filtered);
    }

    function removeCategories(sourceList, selectedList) {
        selectedList.forEach(item => {
            var index = sourceList.findIndex(c => c.id === item.id);
            if (index > -1) {
                sourceList.slice(index, 1);
            }
        })
        return sourceList;
    }

    async function fetchCategories() {
        try {
            const res = await getProperty("product.categories");
            const resCategories = JSON.parse(res.propertyValue);

            let sourceList = [];
            resCategories.forEach((category) => {
                sourceList = [...sourceList,
                {
                    id: category,
                    name: t(category, { ns: 'categories' })
                }
                ]
            });

            //Remove categories already set in the filtered list
            let filtered = sourceList;
            if (filteredCategories && filteredCategories.length > 0 &&
                sourceList && sourceList.length > 0) {
                //sourceList = removeCategories(sourceList, innitialList);
                filtered = sourceList.filter(a => { return filteredCategories.every(b => a.id !== b.id) })
            }

            //sort it
            let sortedList = filtered.sort((a, b) => a.name.localeCompare(b.name));

            setCategories(sortedList);
        } catch (error) {
            showAlert(VariantType.DANGER, t('fetch-categories-error') + error.message);
        }
    }

    /** 
       ** Move item reordering the list
       */
    const reorder = (list, startIndex, endIndex) => {
        const result = Array.from(list);
        const [removed] = result.splice(startIndex, 1);
        result.splice(endIndex, 0, removed);

        return result;
    };

    /**
     * Moves an item from one list to another.
     */
    const move = (source, destination, droppableSource, droppableDestination) => {
        const sourceClone = Array.from(source);
        const destClone = Array.from(destination);
        const [removed] = sourceClone.splice(droppableSource.index, 1);

        destClone.splice(droppableDestination.index, 0, removed);

        const result = {};
        result[droppableSource.droppableId] = sourceClone;
        result[droppableDestination.droppableId] = destClone;

        return result;
    };

    const onDragEnd = (param) => {
        const { source, destination } = param;
        let updatedFilterCategory = [];

        // dropped outside the list
        if (!destination) {
            return;
        }

        if (source.droppableId === destination.droppableId) {
            if (source.droppableId === 'droppableSource') {
                return;
            }

            const items = reorder(
                lists[source.droppableId],
                source.index,
                destination.index
            );

            updatedFilterCategory = items;
            setFilteredCategories(items);

        } else {
            const result = move(
                lists[source.droppableId],
                lists[destination.droppableId],
                source,
                destination
            );

            let sourceList = result.droppableSource;
            if (destination.droppableId === 'droppableSource') {
                sourceList = sourceList.sort((a, b) => a.name.localeCompare(b.name));
            }

            updatedFilterCategory = result.droppableFiltered;

            setFilteredCategories(result.droppableFiltered)
            setCategories(sourceList)
        }

        //return only the ids for update
        let categoryIdList = [];
        updatedFilterCategory.forEach(item => {
            categoryIdList = [...categoryIdList, item.id]
        })

        handleListChange(categoryIdList);
    };

    return (
        <>
            <div className="d-flex flex-row justify-content-center align-items-center gap-2">
                <h6 className="title flex-grow-1 text-center" style={{ minWidth: '180px' }}>{t('ordered-sections-title')}</h6>
                <span className="title flex-grow-1 text-center" style={{ minWidth: '180px' }}>{t('available-sections-title')}</span>
            </div>
            <div className="d-flex flex-row justify-content-evenly gap-2 mt-0 pt-0" disabled={disabled}>
                <DragDropContext onDragEnd={onDragEnd}>
                    <DroppableContainer droppableId="droppableFiltered">
                        {filteredCategories.map((item, index) => (
                            <DraggableItem key={item.id} item={item} index={index} />
                        ))}
                    </DroppableContainer>
                    <DroppableContainer droppableId="droppableSource">
                        {categories.map((item, index) => (
                            <DraggableItem key={item.id} item={item} index={index} />
                        ))}
                    </DroppableContainer>
                </DragDropContext>
            </div >
        </>
    )
}

export function DroppableContainer({ droppableId, children }) {
    return (
        <StrictModeDroppable droppableId={droppableId}>
            {(provided, snapshot) => (
                <div
                    {...provided.droppableProps}
                    ref={provided.innerRef}
                    className='supermarket-sections-box scroll-categories'
                >
                    {children}
                    {provided.placeholder}
                </div>
            )}
        </StrictModeDroppable>
    )
}
//"#c5cbfb"
//#909df4
export function DraggableItem({ item, index }) {

    return (
        <Draggable key={item.id} draggableId={item.id} index={index}>
            {(_provided, _snapshot) => {

                const _style = {
                    margin: "6px",
                    padding: "2px",
                    minWidth: "165px",
                    border: "1.5px solid var(--border-color)",
                    backgroundColor: _snapshot.isDragging ? 'var(--highlight-item-list)' : 'var(--background)',
                    display: "flex",
                    flexDirection: "row",
                    justifyContent: "space-between",
                    alignItems: "center",
                    textWrap: "pretty",
                    ..._provided.draggableProps.style,

                };
                return (

                    <div
                        {..._provided.draggableProps}
                        {..._provided.dragHandleProps}
                        ref={_provided.innerRef}
                        style={_style} className="hover-box" >

                        <span className='text-wrap'>{item.name}</span>
                        <GrDrag className='icon' />
                    </div>
                )
            }}
        </Draggable>
    )
}