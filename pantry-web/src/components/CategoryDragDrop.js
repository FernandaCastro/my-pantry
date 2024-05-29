import { useEffect, useState } from 'react'
import { GrDrag } from "react-icons/gr";
import { DragDropContext, Draggable } from "react-beautiful-dnd";
import { StrictModeDroppable } from '../components/StrictModeDroppable'
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { getProperty } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';

export function CategoryDragDrop({ innitialList }) {

    const { t } = useTranslation(['categories']);

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

            this.setFilteredCategories(items);

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

            setFilteredCategories(result.droppableFiltered)
            setCategories(sourceList)
        }
    };

    return (
        <>
            <div className="d-flex flex-row justify-content-evenly">
                <h6>Filtered</h6>
                <h6>Source</h6>
            </div>
            <div className="d-flex flex-row justify-content-evenly gap-2">
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
                    className='items'
                >
                    {children}
                    {provided.placeholder}
                </div>
            )}
        </StrictModeDroppable>
    )
}

export function DraggableItem({ item, index }) {

    return (
        <Draggable key={item.id} draggableId={item.id} index={index}>
            {(_provided, _snapshot) => {

                const _style = {
                    margin: "6px",
                    padding: "2px",
                    minWidth: "165px",
                    border: "1px solid #909df4",
                    backgroundColor: "#c5cbfb",
                    ..._provided.draggableProps.style,
                };
                return (

                    <div
                        {..._provided.draggableProps}
                        {..._provided.dragHandleProps}
                        ref={_provided.innerRef}
                        style={_style}>
                        <GrDrag />
                        <span className='text-small text-wrap'>{item.name}</span>
                    </div>
                )
            }}
        </Draggable>
    )
}